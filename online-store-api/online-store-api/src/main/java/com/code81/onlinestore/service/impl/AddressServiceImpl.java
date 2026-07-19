package com.code81.onlinestore.service.impl;

import com.code81.onlinestore.dto.address.AddressRequest;
import com.code81.onlinestore.dto.address.AddressResponse;
import com.code81.onlinestore.entity.Address;
import com.code81.onlinestore.entity.Customer;
import com.code81.onlinestore.exception.ResourceNotFoundException;
import com.code81.onlinestore.mapper.AddressMapper;
import com.code81.onlinestore.repository.AddressRepository;
import com.code81.onlinestore.repository.CustomerRepository;
import com.code81.onlinestore.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> list(Long customerId) {
        return addressRepository.findByCustomerIdOrderByIsDefaultDescIdAsc(customerId).stream()
                .map(AddressMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public AddressResponse add(Long customerId, AddressRequest request) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", customerId));

        boolean isFirstAddress = addressRepository.findByCustomerIdOrderByIsDefaultDescIdAsc(customerId).isEmpty();
        if (request.isDefault() || isFirstAddress) {
            unsetExistingDefault(customerId);
            request.setDefault(true);
        }

        Address saved = addressRepository.save(AddressMapper.toEntity(request, customer));
        return AddressMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public AddressResponse update(Long customerId, Long addressId, AddressRequest request) {
        Address address = findOwnedEntity(customerId, addressId);

        if (request.isDefault() && !address.isDefault()) {
            unsetExistingDefault(customerId);
        }
        AddressMapper.updateEntity(address, request);
        return AddressMapper.toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void delete(Long customerId, Long addressId) {
        Address address = findOwnedEntity(customerId, addressId);
        boolean wasDefault = address.isDefault();
        addressRepository.delete(address);

        if (wasDefault) {
            addressRepository.findByCustomerIdOrderByIsDefaultDescIdAsc(customerId).stream()
                    .findFirst()
                    .ifPresent(next -> {
                        next.setDefault(true);
                        addressRepository.save(next);
                    });
        }
    }

    private void unsetExistingDefault(Long customerId) {
        addressRepository.findByCustomerIdOrderByIsDefaultDescIdAsc(customerId).stream()
                .filter(Address::isDefault)
                .forEach(a -> {
                    a.setDefault(false);
                    addressRepository.save(a);
                });
    }

    private Address findOwnedEntity(Long customerId, Long addressId) {
        return addressRepository.findByIdAndCustomerId(addressId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", addressId));
    }
}

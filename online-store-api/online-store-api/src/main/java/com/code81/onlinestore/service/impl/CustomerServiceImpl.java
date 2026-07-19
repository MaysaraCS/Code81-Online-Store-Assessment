package com.code81.onlinestore.service.impl;

import com.code81.onlinestore.dto.customer.ChangePasswordRequest;
import com.code81.onlinestore.dto.customer.CustomerProfileUpdateRequest;
import com.code81.onlinestore.dto.customer.CustomerRegisterRequest;
import com.code81.onlinestore.dto.customer.CustomerResponse;
import com.code81.onlinestore.entity.Customer;
import com.code81.onlinestore.exception.DuplicateResourceException;
import com.code81.onlinestore.exception.InvalidCredentialsException;
import com.code81.onlinestore.exception.ResourceNotFoundException;
import com.code81.onlinestore.mapper.CustomerMapper;
import com.code81.onlinestore.repository.CustomerRepository;
import com.code81.onlinestore.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public CustomerResponse register(CustomerRegisterRequest request) {
        if (customerRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateResourceException("An account with email '" + request.getEmail() + "' already exists");
        }

        Customer customer = Customer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .build();

        return CustomerMapper.toResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getProfile(Long customerId) {
        return CustomerMapper.toResponse(findEntity(customerId));
    }

    @Override
    @Transactional
    public CustomerResponse updateProfile(Long customerId, CustomerProfileUpdateRequest request) {
        Customer customer = findEntity(customerId);
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhone(request.getPhone());
        return CustomerMapper.toResponse(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public void changePassword(Long customerId, ChangePasswordRequest request) {
        Customer customer = findEntity(customerId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), customer.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }
        customer.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        customerRepository.save(customer);
    }

    private Customer findEntity(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }
}

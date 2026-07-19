package com.code81.onlinestore.service;

import com.code81.onlinestore.dto.address.AddressRequest;
import com.code81.onlinestore.dto.address.AddressResponse;

import java.util.List;

public interface AddressService {

    List<AddressResponse> list(Long customerId);

    AddressResponse add(Long customerId, AddressRequest request);

    AddressResponse update(Long customerId, Long addressId, AddressRequest request);

    void delete(Long customerId, Long addressId);
}

package com.code81.onlinestore.service;

import com.code81.onlinestore.dto.customer.ChangePasswordRequest;
import com.code81.onlinestore.dto.customer.CustomerProfileUpdateRequest;
import com.code81.onlinestore.dto.customer.CustomerRegisterRequest;
import com.code81.onlinestore.dto.customer.CustomerResponse;

public interface CustomerService {

    CustomerResponse register(CustomerRegisterRequest request);

    CustomerResponse getProfile(Long customerId);

    CustomerResponse updateProfile(Long customerId, CustomerProfileUpdateRequest request);

    void changePassword(Long customerId, ChangePasswordRequest request);
}

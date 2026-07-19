package com.code81.onlinestore.service;

import com.code81.onlinestore.dto.auth.AuthResponse;
import com.code81.onlinestore.dto.auth.LoginRequest;
import com.code81.onlinestore.dto.auth.RefreshRequest;

public interface AuthService {

    AuthResponse loginCustomer(LoginRequest request);

    AuthResponse loginStaff(LoginRequest request);

    AuthResponse refresh(RefreshRequest request);

    void logout(RefreshRequest request);
}

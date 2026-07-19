package com.code81.onlinestore.service.impl;

import com.code81.onlinestore.dto.auth.AuthResponse;
import com.code81.onlinestore.dto.auth.LoginRequest;
import com.code81.onlinestore.dto.auth.RefreshRequest;
import com.code81.onlinestore.entity.Customer;
import com.code81.onlinestore.entity.OwnerType;
import com.code81.onlinestore.entity.RefreshToken;
import com.code81.onlinestore.entity.StaffUser;
import com.code81.onlinestore.exception.InvalidCredentialsException;
import com.code81.onlinestore.exception.TokenRefreshException;
import com.code81.onlinestore.repository.CustomerRepository;
import com.code81.onlinestore.repository.RefreshTokenRepository;
import com.code81.onlinestore.repository.StaffUserRepository;
import com.code81.onlinestore.security.JwtService;
import com.code81.onlinestore.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CustomerRepository customerRepository;
    private final StaffUserRepository staffUserRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${app.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Override
    @Transactional
    public AuthResponse loginCustomer(LoginRequest request) {
        Customer customer = customerRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), customer.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return issueTokens(customer.getId(), customer.getEmail(), OwnerType.CUSTOMER, "CUSTOMER");
    }

    @Override
    @Transactional
    public AuthResponse loginStaff(LoginRequest request) {
        StaffUser staff = staffUserRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!staff.isActive()) {
            throw new InvalidCredentialsException("This staff account has been deactivated");
        }
        if (!passwordEncoder.matches(request.getPassword(), staff.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return issueTokens(staff.getId(), staff.getEmail(), OwnerType.STAFF, staff.getRole().name());
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new TokenRefreshException("Refresh token not recognized"));

        if (stored.isRevoked()) {
            throw new TokenRefreshException("Refresh token has been revoked - please log in again");
        }
        if (stored.isExpired()) {
            throw new TokenRefreshException("Refresh token has expired - please log in again");
        }

        String username;
        String role;
        if (stored.getOwnerType() == OwnerType.CUSTOMER) {
            Customer customer = customerRepository.findById(stored.getOwnerId())
                    .orElseThrow(() -> new TokenRefreshException("Account no longer exists"));
            username = customer.getEmail();
            role = "CUSTOMER";
        } else {
            StaffUser staff = staffUserRepository.findById(stored.getOwnerId())
                    .orElseThrow(() -> new TokenRefreshException("Account no longer exists"));
            if (!staff.isActive()) {
                throw new TokenRefreshException("This staff account has been deactivated");
            }
            username = staff.getEmail();
            role = staff.getRole().name();
        }

        // Rotate: the old refresh token is single-use.
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        return issueTokens(stored.getOwnerId(), username, stored.getOwnerType(), role);
    }

    @Override
    @Transactional
    public void logout(RefreshRequest request) {
        refreshTokenRepository.findByToken(request.getRefreshToken())
                .ifPresent(t -> {
                    t.setRevoked(true);
                    refreshTokenRepository.save(t);
                });
    }

    private AuthResponse issueTokens(Long ownerId, String username, OwnerType ownerType, String role) {
        String accessToken = jwtService.generateAccessToken(ownerId, username, ownerType, role);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .ownerType(ownerType)
                .ownerId(ownerId)
                .expiryDate(Instant.now().plusMillis(refreshTokenExpirationMs))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresInSeconds(jwtService.getAccessTokenExpirationSeconds())
                .role(role)
                .userId(ownerId)
                .build();
    }
}

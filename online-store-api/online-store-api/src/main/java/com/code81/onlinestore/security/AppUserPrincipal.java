package com.code81.onlinestore.security;

import com.code81.onlinestore.entity.OwnerType;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * The single principal type used across the app, for both customers and
 * staff. Customers always carry ROLE_CUSTOMER; staff carry ROLE_ADMIN /
 * ROLE_STORE_MANAGER / ROLE_SUPPORT_AGENT based on their Role enum.
 * Populated by JwtAuthenticationFilter from the token claims - there is no
 * per-request DB lookup, which keeps the API stateless.
 */
@Getter
public class AppUserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final OwnerType ownerType;
    private final Collection<? extends GrantedAuthority> authorities;

    public AppUserPrincipal(Long id, String username, OwnerType ownerType, String role) {
        this.id = id;
        this.username = username;
        this.ownerType = ownerType;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    public boolean isStaff() {
        return ownerType == OwnerType.STAFF;
    }

    public boolean isCustomer() {
        return ownerType == OwnerType.CUSTOMER;
    }

    @Override
    public String getPassword() {
        return null; // never needed post-authentication; tokens are already verified
    }

    @Override
    public String getUsername() {
        return username;
    }
}

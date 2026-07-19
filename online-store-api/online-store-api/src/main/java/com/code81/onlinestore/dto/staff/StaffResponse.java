package com.code81.onlinestore.dto.staff;

import com.code81.onlinestore.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class StaffResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private boolean active;
    private Instant createdAt;
}

package com.code81.onlinestore.mapper;

import com.code81.onlinestore.dto.staff.StaffResponse;
import com.code81.onlinestore.entity.StaffUser;

public final class StaffMapper {

    private StaffMapper() {
    }

    public static StaffResponse toResponse(StaffUser staff) {
        return StaffResponse.builder()
                .id(staff.getId())
                .username(staff.getUsername())
                .email(staff.getEmail())
                .role(staff.getRole())
                .active(staff.isActive())
                .createdAt(staff.getCreatedAt())
                .build();
    }
}

package com.code81.onlinestore.service.impl;

import com.code81.onlinestore.dto.common.PageResponse;
import com.code81.onlinestore.dto.staff.StaffCreateRequest;
import com.code81.onlinestore.dto.staff.StaffResponse;
import com.code81.onlinestore.dto.staff.StaffUpdateRequest;
import com.code81.onlinestore.entity.StaffUser;
import com.code81.onlinestore.exception.DuplicateResourceException;
import com.code81.onlinestore.exception.ResourceNotFoundException;
import com.code81.onlinestore.mapper.StaffMapper;
import com.code81.onlinestore.repository.StaffUserRepository;
import com.code81.onlinestore.service.ActivityLogService;
import com.code81.onlinestore.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffUserRepository staffUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;

    @Override
    @Transactional
    public StaffResponse create(StaffCreateRequest request) {
        if (staffUserRepository.existsByUsernameIgnoreCase(request.getUsername())) {
            throw new DuplicateResourceException("Username '" + request.getUsername() + "' is already taken");
        }
        if (staffUserRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already registered");
        }

        StaffUser staff = StaffUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .build();
        StaffUser saved = staffUserRepository.save(staff);

        activityLogService.log("CREATE_STAFF", "StaffUser", saved.getId(),
                "Created staff account '" + saved.getUsername() + "' with role " + saved.getRole());

        return StaffMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public StaffResponse update(Long id, StaffUpdateRequest request) {
        StaffUser staff = findEntity(id);
        staff.setRole(request.getRole());
        staff.setActive(request.getActive());
        StaffUser saved = staffUserRepository.save(staff);

        activityLogService.log("UPDATE_STAFF", "StaffUser", saved.getId(),
                "Set role=" + saved.getRole() + ", active=" + saved.isActive());

        return StaffMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public StaffResponse getById(Long id) {
        return StaffMapper.toResponse(findEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StaffResponse> list(Pageable pageable) {
        var page = staffUserRepository.findAll(pageable).map(StaffMapper::toResponse);
        return PageResponse.from(page);
    }

    private StaffUser findEntity(Long id) {
        return staffUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff user", id));
    }
}

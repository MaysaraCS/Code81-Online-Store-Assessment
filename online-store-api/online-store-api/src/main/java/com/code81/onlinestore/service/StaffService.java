package com.code81.onlinestore.service;

import com.code81.onlinestore.dto.common.PageResponse;
import com.code81.onlinestore.dto.staff.StaffCreateRequest;
import com.code81.onlinestore.dto.staff.StaffResponse;
import com.code81.onlinestore.dto.staff.StaffUpdateRequest;
import org.springframework.data.domain.Pageable;

public interface StaffService {

    StaffResponse create(StaffCreateRequest request);

    StaffResponse update(Long id, StaffUpdateRequest request);

    StaffResponse getById(Long id);

    PageResponse<StaffResponse> list(Pageable pageable);
}

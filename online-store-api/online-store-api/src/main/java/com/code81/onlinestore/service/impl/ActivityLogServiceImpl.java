package com.code81.onlinestore.service.impl;

import com.code81.onlinestore.dto.activitylog.ActivityLogResponse;
import com.code81.onlinestore.dto.common.PageResponse;
import com.code81.onlinestore.entity.ActivityLog;
import com.code81.onlinestore.mapper.ActivityLogMapper;
import com.code81.onlinestore.repository.ActivityLogRepository;
import com.code81.onlinestore.repository.StaffUserRepository;
import com.code81.onlinestore.security.SecurityUtils;
import com.code81.onlinestore.service.ActivityLogService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

    private static final Logger log = LoggerFactory.getLogger(ActivityLogServiceImpl.class);

    private final ActivityLogRepository activityLogRepository;
    private final StaffUserRepository staffUserRepository;

    @Override
    @Transactional
    public void log(String action, String entityType, Long entityId, String details) {
        Long staffId = SecurityUtils.getCurrentStaffId();
        if (staffId == null) {
            log.warn("Skipped activity log '{}' on {}#{} - no staff principal in context", action, entityType, entityId);
            return;
        }
        ActivityLog entry = ActivityLog.builder()
                .staffUser(staffUserRepository.getReferenceById(staffId)) // proxy reference, no extra SELECT
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .details(details)
                .build();
        activityLogRepository.save(entry);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ActivityLogResponse> list(Pageable pageable) {
        var page = activityLogRepository.findAllByOrderByTimestampDesc(pageable).map(ActivityLogMapper::toResponse);
        return PageResponse.from(page);
    }
}

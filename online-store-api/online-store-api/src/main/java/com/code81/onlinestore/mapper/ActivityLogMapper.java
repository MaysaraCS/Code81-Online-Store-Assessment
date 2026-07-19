package com.code81.onlinestore.mapper;

import com.code81.onlinestore.dto.activitylog.ActivityLogResponse;
import com.code81.onlinestore.entity.ActivityLog;

public final class ActivityLogMapper {

    private ActivityLogMapper() {
    }

    public static ActivityLogResponse toResponse(ActivityLog log) {
        return ActivityLogResponse.builder()
                .id(log.getId())
                .staffUserId(log.getStaffUser().getId())
                .staffUsername(log.getStaffUser().getUsername())
                .action(log.getAction())
                .entityType(log.getEntityType())
                .entityId(log.getEntityId())
                .details(log.getDetails())
                .timestamp(log.getTimestamp())
                .build();
    }
}

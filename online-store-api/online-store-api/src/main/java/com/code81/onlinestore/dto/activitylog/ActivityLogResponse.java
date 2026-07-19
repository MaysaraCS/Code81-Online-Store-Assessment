package com.code81.onlinestore.dto.activitylog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class ActivityLogResponse {
    private Long id;
    private Long staffUserId;
    private String staffUsername;
    private String action;
    private String entityType;
    private Long entityId;
    private String details;
    private Instant timestamp;
}

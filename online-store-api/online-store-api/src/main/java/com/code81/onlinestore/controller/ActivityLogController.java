package com.code81.onlinestore.controller;

import com.code81.onlinestore.dto.activitylog.ActivityLogResponse;
import com.code81.onlinestore.dto.common.PageResponse;
import com.code81.onlinestore.service.ActivityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Activity Logs", description = "Audit trail of staff actions (admin only)")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    @Operation(summary = "List activity log entries, most recent first")
    public ResponseEntity<PageResponse<ActivityLogResponse>> list(@PageableDefault(size = 30) Pageable pageable) {
        return ResponseEntity.ok(activityLogService.list(pageable));
    }
}

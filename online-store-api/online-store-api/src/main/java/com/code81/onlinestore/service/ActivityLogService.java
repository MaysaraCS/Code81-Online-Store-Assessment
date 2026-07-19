package com.code81.onlinestore.service;

import com.code81.onlinestore.dto.activitylog.ActivityLogResponse;
import com.code81.onlinestore.dto.common.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ActivityLogService {

    /**
     * Records one staff action. Reads "who did it" from the current security
     * context (SecurityUtils) rather than taking a staffId parameter, so
     * calling this from deep inside CategoryServiceImpl/ProductServiceImpl
     * doesn't require threading an extra parameter through every method
     * signature. If no staff principal is present (shouldn't normally happen
     * here, since these call sites are all behind staff-only endpoints), the
     * call is silently skipped rather than throwing - logging should never
     * be the reason a real business operation fails.
     */
    void log(String action, String entityType, Long entityId, String details);

    PageResponse<ActivityLogResponse> list(Pageable pageable);
}

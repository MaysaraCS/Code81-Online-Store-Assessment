package com.code81.onlinestore.security;

import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Returns the currently authenticated principal, or null if the request
     * is unauthenticated (e.g. public catalog browsing).
     */
    public static AppUserPrincipal getCurrentPrincipal() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AppUserPrincipal principal)) {
            return null;
        }
        return principal;
    }

    /** Returns the current staff user's id, or null if the caller isn't staff (or isn't authenticated). */
    public static Long getCurrentStaffId() {
        AppUserPrincipal principal = getCurrentPrincipal();
        return (principal != null && principal.isStaff()) ? principal.getId() : null;
    }
}

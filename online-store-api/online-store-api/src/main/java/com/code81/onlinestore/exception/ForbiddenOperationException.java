package com.code81.onlinestore.exception;

/** Authenticated, but not allowed to touch this specific resource (e.g. another customer's order). */
public class ForbiddenOperationException extends RuntimeException {
    public ForbiddenOperationException(String message) {
        super(message);
    }
}

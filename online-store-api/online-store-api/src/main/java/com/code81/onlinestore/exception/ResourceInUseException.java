package com.code81.onlinestore.exception;

/**
 * Thrown when a caller tries to delete a resource that other records still
 * reference, e.g. a Category that still has Products assigned to it.
 */
public class ResourceInUseException extends RuntimeException {

    public ResourceInUseException(String message) {
        super(message);
    }
}

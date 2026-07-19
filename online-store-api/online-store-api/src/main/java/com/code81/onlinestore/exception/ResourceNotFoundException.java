package com.code81.onlinestore.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String entity, Object identifier) {
        super(entity + " not found with id: " + identifier);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

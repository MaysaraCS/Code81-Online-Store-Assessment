package com.code81.onlinestore.entity;

/**
 * Distinguishes which table a RefreshToken (or a generic principal) belongs
 * to, since customers and staff are stored separately rather than in one
 * polymorphic user table.
 */
public enum OwnerType {
    CUSTOMER,
    STAFF
}

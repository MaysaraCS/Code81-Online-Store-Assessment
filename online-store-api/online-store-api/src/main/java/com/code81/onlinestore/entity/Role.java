package com.code81.onlinestore.entity;

/**
 * Staff roles only. Customers are not "roled" - they always have the same
 * permissions (their own data). See ERD/use-case notes: Administrator,
 * StoreManager, SupportAgent.
 */
public enum Role {
    ADMIN,
    STORE_MANAGER,
    SUPPORT_AGENT
}

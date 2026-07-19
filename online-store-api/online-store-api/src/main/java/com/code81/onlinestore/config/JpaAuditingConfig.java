package com.code81.onlinestore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Enables automatic population of @CreatedDate / @LastModifiedDate fields
 * on any entity that uses @EntityListeners(AuditingEntityListener.class).
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}

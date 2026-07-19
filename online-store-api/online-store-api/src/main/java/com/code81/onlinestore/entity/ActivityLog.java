package com.code81.onlinestore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "activity_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_user_id", nullable = false)
    private StaffUser staffUser;

    /** Short, consistent action codes, e.g. "CREATE_PRODUCT", "UPDATE_ORDER_STATUS". */
    @Column(nullable = false, length = 50)
    private String action;

    /** The entity type affected, e.g. "Product", "Order". */
    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(length = 500)
    private String details;

    @Column(nullable = false)
    @Builder.Default
    private Instant timestamp = Instant.now();
}

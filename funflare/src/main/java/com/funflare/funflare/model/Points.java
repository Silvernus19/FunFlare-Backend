// src/main/java/com/funflare/entity/Points.java
package com.funflare.funflare.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "points")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Points {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "points_id")
    private Long pointsId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "purchase_id", nullable = false)
    private Long purchaseId;

    @Column(name = "purchase_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal purchaseAmount;

    @Column(name = "points_created", nullable = false, precision = 10, scale = 2)
    private BigDecimal pointsCreated;

    @Column(name = "points_redeemed", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal pointsRedeemed = BigDecimal.ZERO;

    @Column(name = "points_balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal pointsBalance;

    @Column(name = "event_related", nullable = false)
    @Builder.Default
    private boolean eventRelated = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;
}
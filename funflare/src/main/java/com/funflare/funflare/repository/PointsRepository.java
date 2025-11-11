// src/main/java/com/funflare/funflare/repository/PointsRepository.java
package com.funflare.funflare.repository;

import com.funflare.funflare.model.Points;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PointsRepository extends JpaRepository<Points, Long> {

    @Query("SELECT p FROM Points p WHERE p.userId = :userId ORDER BY p.createdAt ASC")
    List<Points> findByUserIdOrderByCreatedAtAsc(@Param("userId") Long userId);


    @Query("""
           SELECT COALESCE(SUM(p.pointsCreated - p.pointsRedeemed), 0)
           FROM Points p
           WHERE p.userId = :userId
           """)
    BigDecimal sumPointsBalanceByUserId(@Param("userId") Long userId);


    boolean existsByPurchaseId(Long purchaseId);
    Optional<Points> findByPurchaseId(Long purchaseId);


    Optional<Points> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}
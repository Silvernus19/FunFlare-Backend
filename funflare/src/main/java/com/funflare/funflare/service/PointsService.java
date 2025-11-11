package com.funflare.funflare.service;

import com.funflare.funflare.model.Points;
import com.funflare.funflare.model.Purchases;
import com.funflare.funflare.repository.PointsRepository;
//import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PointsService {

    private static final Logger log = LoggerFactory.getLogger(PointsService.class);
    private final PointsRepository pointsRepository;

    // Fixed threshold: > 2 KSH
    private static final BigDecimal MIN_PURCHASE_FOR_POINTS = new BigDecimal("2");

    public PointsService(PointsRepository pointsRepository) {
        this.pointsRepository = pointsRepository;
    }

    /**
     * Returns 1 point if purchase > 2 KSH, else 0
     */
    public BigDecimal calculatePoints(BigDecimal purchaseAmountKsh) {
        return purchaseAmountKsh.compareTo(MIN_PURCHASE_FOR_POINTS) > 0
                ? BigDecimal.ONE
                : BigDecimal.ZERO;
    }

    // ──────────────────────────────────────────────────────────────
    //  AWARD POINTS (MPESA / WALLET) – called from callback
    // ──────────────────────────────────────────────────────────────
    @Transactional
    public Points awardPointsForPurchase(Purchases purchase) {
        if (purchase.getStatus() != Purchases.Status.COMPLETED) {
            log.info("[POINTS] Purchase {} not COMPLETED – skipping award", purchase.getId());
            return null;
        }

        // Prevent duplicate award
        if (pointsRepository.existsByPurchaseId(purchase.getId())) {
            log.info("[POINTS] Points already awarded for purchase {}", purchase.getId());
            return pointsRepository.findByPurchaseId(purchase.getId()).orElse(null);
        }

        BigDecimal amount = BigDecimal.valueOf(purchase.getTotalAmount());
        BigDecimal points = calculatePoints(amount);

        if (points.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("[POINTS] Purchase {} (KES {}) ≤ 2 KSH – no points", purchase.getId(), amount);
            return null;
        }

        Points pts = Points.builder()
                .userId(purchase.getUser().getId())
                .purchaseId(purchase.getId())
                .purchaseAmount(amount)
                .pointsCreated(points)
                .pointsBalance(points)
                .build();

        Points saved = pointsRepository.save(pts);
        log.info("[POINTS] AWARDED 1 point | purchase={} | user={} | amount={} KES",
                purchase.getId(), purchase.getUser().getId(), amount);
        return saved;
    }

    // ──────────────────────────────────────────────────────────────
    //  GET USER BALANCE – used before POINTS purchase
    // ──────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public BigDecimal getUserPointsBalance(Long userId) {
        BigDecimal balance = pointsRepository.sumPointsBalanceByUserId(userId);

        log.debug("[POINTS] User {} points balance = {}", userId, balance);
        return balance;
    }
    // ──────────────────────────────────────────────────────────────
    //  DEDUCT POINTS – used during POINTS purchase
    // ──────────────────────────────────────────────────────────────
    @Transactional
    public void deductPoints(Long userId, BigDecimal pointsToDeduct, Long purchaseId) {
        log.info("[POINTS] DEDUCTING {} points from user {} for purchase {}", pointsToDeduct, userId, purchaseId);

        BigDecimal totalBalance = pointsRepository.sumPointsBalanceByUserId(userId);
        if (totalBalance.compareTo(pointsToDeduct) < 0) {
            throw new IllegalStateException(
                    String.format("Insufficient points: need %.0f, have %.0f", pointsToDeduct, totalBalance));
        }

        List<Points> pointsList = pointsRepository.findByUserIdOrderByCreatedAtAsc(userId);
        BigDecimal remaining = pointsToDeduct;

        for (Points p : pointsList) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal available = p.getPointsCreated().subtract(p.getPointsRedeemed());
            if (available.compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal deduct = available.min(remaining);
            p.setPointsRedeemed(p.getPointsRedeemed().add(deduct));
            p.setPointsBalance(p.getPointsCreated().subtract(p.getPointsRedeemed()));
            pointsRepository.save(p);

            remaining = remaining.subtract(deduct);
        }

        log.info("[POINTS] SUCCESS – deducted {} points | user={}", pointsToDeduct, userId);
    }
}
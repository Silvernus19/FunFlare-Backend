package com.funflare.funflare.service;

import com.funflare.funflare.model.Points;
import com.funflare.funflare.model.Purchases;
import com.funflare.funflare.repository.PointsRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

    @Transactional
    public Points awardPointsForPurchase(Purchases purchase) {
        if (purchase.getStatus() != Purchases.Status.COMPLETED) {
            log.info("Purchase {} not COMPLETED – skipping points", purchase.getId());
            return null;
        }

        BigDecimal amount = BigDecimal.valueOf(purchase.getTotalAmount());
        BigDecimal points = calculatePoints(amount);

        if (points.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("Purchase {} (KES {}) ≤ 2 KSH – no points awarded", purchase.getId(), amount);
            return null;
        }

        Points pts = Points.builder()
                .userId(purchase.getUser().getId())
                .purchaseId(purchase.getId())
                .purchaseAmount(amount)
                .pointsCreated(points)     // 1
                .pointsBalance(points)     // 1
                .build();

        Points saved = pointsRepository.save(pts);
        log.info("Awarded 1 point for purchase {} (user {}) – amount: KES {}",
                purchase.getId(), purchase.getUser().getId(), amount);
        return saved;
    }
}
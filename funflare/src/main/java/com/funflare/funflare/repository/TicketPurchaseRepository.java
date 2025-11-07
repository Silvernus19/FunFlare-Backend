package com.funflare.funflare.repository;

import com.funflare.funflare.model.TicketPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * TicketPurchaseRepository
 * -------------------------------------------------
 * Why these methods?
 * 1. findByPurchaseId → Email & download all QRs in one DB call
 * 2. findByQrCodeUid  → Gate scanner validates in <50ms
 * -------------------------------------------------
 */
public interface TicketPurchaseRepository extends JpaRepository<TicketPurchase, Long> {

    /**
     * Get every ticket belonging to a purchase.
     * Used right after STK push to attach QR PNGs to the email.
     */
    List<TicketPurchase> findByPurchaseId(Long purchaseId);

    /**
     * Gate scanner will POST the QR string → we look it up.
     * Returns the full ticket with event name, guest name, status, etc.
     */
    Optional<TicketPurchase> findByQrCodeUid(String qrCodeUid);

    /**
     * BONUS: Quick check if a purchase has ANY valid tickets.
     * (Optional – handy for future “resend tickets” button)
     */
    @Query("SELECT COUNT(tp) > 0 FROM TicketPurchase tp " +
            "WHERE tp.purchase.id = :purchaseId " +
            "AND tp.status = 'VALID'")
    boolean hasValidTickets(@Param("purchaseId") Long purchaseId);
}
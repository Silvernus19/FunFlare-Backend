package com.funflare.funflare.service;

import com.funflare.funflare.dto.PurchaseCreateDTO;
import com.funflare.funflare.dto.PurchaseCreateDTO.TicketSelectDTO;
import com.funflare.funflare.model.*;
import com.funflare.funflare.model.Purchases.PaymentMethod;
import com.funflare.funflare.model.Purchases.Status;
import com.funflare.funflare.repository.*;
import com.funflare.funflare.util.QrCodeGenerator;
import com.google.zxing.WriterException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseService.class);

    private final PurchasesRepository purchasesRepository;
    private final TicketPurchaseRepository ticketPurchaseRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final MpesaService mpesaService;
    private final EntityManager entityManager;
    private final PointsService pointsService; // ← NEW

    @Autowired
    public PurchaseService(
            PurchasesRepository purchasesRepository,
            TicketPurchaseRepository ticketPurchaseRepository,
            TicketRepository ticketRepository,
            UserRepository userRepository,
            WalletRepository walletRepository,
            MpesaService mpesaService,
            EntityManager entityManager,
            PointsService pointsService
    ) {
        this.purchasesRepository = purchasesRepository;
        this.ticketPurchaseRepository = ticketPurchaseRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.mpesaService = mpesaService;
        this.entityManager = entityManager;
        this.pointsService = pointsService;
    }

    @Transactional
    public Purchases createPurchase(PurchaseCreateDTO dto, Long userId) {
        logger.info("Creating purchase for user {} on event {}", userId, dto.getEventId());

        User buyer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        validateTicketsAvailability(dto.getEventId(), dto.getSelectedTickets());

        BigDecimal calculatedTotal = dto.getSelectedTickets().stream()
                .map(t -> BigDecimal.valueOf(t.getQuantity()).multiply(t.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalQuantity = dto.getSelectedTickets().stream()
                .mapToInt(TicketSelectDTO::getQuantity)
                .sum();

        // ---------- 1. CREATE PURCHASE ----------
        Purchases purchase = new Purchases();
        purchase.setUser(buyer);
        purchase.setQuantity(totalQuantity);
        purchase.setTotalAmount(calculatedTotal.doubleValue());
        purchase.setStatus(Status.PENDING);
        purchase.setPaymentMethod(PaymentMethod.valueOf(dto.getPaymentMethod().toUpperCase()));
        purchase.setPurchaseEmail(dto.getPurchaseEmail());
        purchase.setPhoneNumber(dto.getPaymentMethod().equalsIgnoreCase("MPESA") ? dto.getPhoneNumber() : null);
        purchase.setGuestName(dto.getGuestName());
        purchase.setPurchaseDate(OffsetDateTime.now(ZoneOffset.UTC));
        purchase = purchasesRepository.save(purchase);

        // ---------- 2. CREATE TICKET LINE ITEMS ----------
        for (TicketSelectDTO sel : dto.getSelectedTickets()) {
            Ticket ticket = ticketRepository
                    .findByEventIdAndType(dto.getEventId(), sel.getTicketTypeEnum())
                    .orElseThrow(() -> new RuntimeException("Ticket type not found: " + sel.getTicketType()));

            for (int i = 0; i < sel.getQuantity(); i++) {
                TicketPurchase tp = new TicketPurchase();
                tp.setPurchase(purchase);
                tp.setTicket(ticket);
                tp.setTicketPrice(sel.getPrice().doubleValue());
                tp.setQrCodeUid(UUID.randomUUID().toString());
                tp.setStatus(TicketPurchase.Status.VALID);
                tp.setGuestName(dto.getGuestName());
                tp.setGuestEmail(dto.getPurchaseEmail());
                tp.setGuestPhone(dto.getPhoneNumber());
                tp.setCheckedInAt(null);
                ticketPurchaseRepository.save(tp);

                // ---------- 3. GENERATE & STORE QR ----------
                generateQrForTicket(tp);

                // ---------- 4. REDUCE STOCK (PESSIMISTIC) ----------
                Ticket lockedTicket = entityManager.find(Ticket.class, ticket.getId(), LockModeType.PESSIMISTIC_WRITE);
                if (lockedTicket.getQuantity() <= 0) {
                    throw new RuntimeException("Stock depleted for " + sel.getTicketType());
                }
                lockedTicket.setQuantity(lockedTicket.getQuantity() - 1);
                lockedTicket.setQuantitySold(lockedTicket.getQuantitySold() + 1);
                ticketRepository.save(lockedTicket);
            }
        }

        // ---------- 5. PAYMENT ----------
        try {
            processPayment(purchase, dto.getPaymentMethod(), buyer);

            // DO NOT mark as COMPLETED here for MPESA
            // Callback will handle COMPLETED + points

        } catch (Exception e) {
            purchase.setStatus(Status.CANCELLED);
            purchasesRepository.save(purchase);
            logger.error("Payment initiation failed for purchase {}", purchase.getId(), e);
            throw new RuntimeException("Payment failed: " + e.getMessage(), e);
        }

        sendPurchaseConfirmation(purchase, dto.getPurchaseEmail());
        logger.info("Purchase {} created (PENDING) for {} tickets. Awaiting MPESA callback.", purchase.getId(), totalQuantity);
        return purchase;
    }

    private void generateQrForTicket(TicketPurchase tp) {
        String qrText = tp.getQrCodeUid();
        try {
            byte[] png = QrCodeGenerator.generatePng(qrText, 400, 400);
            tp.setQrCodeImage(png);
            ticketPurchaseRepository.save(tp);
            logger.info("QR generated for ticket_purchase_id={} ({} bytes)", tp.getId(), png.length);
        } catch (WriterException | IOException e) {
            logger.error("QR generation failed for ticket_purchase_id={}", tp.getId(), e);
            // Non-critical – continue
        }
    }

    // ──────────────────────────────────────────────────────────────
    //  Validation, Payment, Email
    // ──────────────────────────────────────────────────────────────
    private void validateTicketsAvailability(Long eventId, List<TicketSelectDTO> selectedTickets) {
        ZoneOffset eat = ZoneOffset.of("+03:00");
        OffsetDateTime now = OffsetDateTime.now(eat);

        for (TicketSelectDTO sel : selectedTickets) {
            Ticket ticket = ticketRepository
                    .findByEventIdAndType(eventId, sel.getTicketTypeEnum())
                    .orElseThrow(() -> new RuntimeException("Ticket type not found: " + sel.getTicketType()));

            OffsetDateTime start = ticket.getSaleStartDate()
                    .atTime(ticket.getSaleStartTime())
                    .atOffset(eat);
            OffsetDateTime end = ticket.getSaleEndDate()
                    .atTime(ticket.getSaleEndTime() != null ? ticket.getSaleEndTime() : LocalTime.MAX)
                    .atOffset(eat);

            if (now.isBefore(start) || now.isAfter(end)) {
                throw new SalePeriodExpiredException(
                        "Tickets for " + sel.getTicketType() + " are only on sale from " +
                                start + " to " + end + " (EAT). Current time: " + now
                );
            }

            if (ticket.getQuantity() < sel.getQuantity()) {
                throw new InsufficientStockException("Only " + ticket.getQuantity() + " left for " + sel.getTicketType());
            }
        }
    }

    private void processPayment(Purchases purchase, String method, User buyer) {
        PaymentMethod pm = PaymentMethod.valueOf(method.toUpperCase());
        double amount = purchase.getTotalAmount();

        if (pm != PaymentMethod.MPESA) {
            throw new IllegalArgumentException("Only MPESA is supported at the moment");
        }

        if (purchase.getPhoneNumber() == null || purchase.getPhoneNumber().isBlank()) {
            throw new InvalidPaymentException("Phone number is required for MPESA");
        }
        if (!purchase.getPhoneNumber().matches("^254[17]\\d{8}$")) {
            throw new InvalidPaymentException("Invalid phone format. Use 2547XXXXXXXX");
        }

        String accountRef = "TICKET-" + purchase.getId();
        String transactionDesc = "Event Ticket Payment #" + purchase.getId();

        String result = mpesaService.initiateStkPush(
                purchase.getPhoneNumber(),
                (int) Math.round(amount),
                accountRef,
                transactionDesc
        );

        logger.info("STK Push result: {}", result);

        if (!result.startsWith("Success:")) {
            throw new InvalidPaymentException("STK Push failed: " + result);
        }

        String checkoutId = result.substring(result.indexOf("Checkout ID: ") + 13).trim();
        purchase.setTransactionRef(checkoutId);
        purchasesRepository.save(purchase);

        logger.info("STK Push sent. Awaiting callback for Checkout ID: {}", checkoutId);
    }

    private void sendPurchaseConfirmation(Purchases purchase, String email) {
        logger.info("=== FUNFLARE TICKET CONFIRMATION ===");
        logger.info("To: {}", email);
        logger.info("Purchase ID: {}", purchase.getId());
        logger.info("Total: KES {}", purchase.getTotalAmount());
        logger.info("Status: {}", purchase.getStatus());

        List<TicketPurchase> tickets = ticketPurchaseRepository.findByPurchaseId(purchase.getId());
        for (int i = 0; i < tickets.size(); i++) {
            TicketPurchase tp = tickets.get(i);
            logger.info("Ticket {}: {} | QR UID: {} | PNG {} bytes",
                    i + 1,
                    tp.getTicket().getType(),
                    tp.getQrCodeUid(),
                    tp.getQrCodeImage() != null ? tp.getQrCodeImage().length : 0);
        }
        logger.info("=== END EMAIL ===");
        // TODO: Real email with PNG attachments
    }

    // ──────────────────────────────────────────────────────────────
    //  Custom Exceptions
    // ──────────────────────────────────────────────────────────────
    public static class InsufficientStockException extends RuntimeException {
        public InsufficientStockException(String message) { super(message); }
    }
    public static class SalePeriodExpiredException extends RuntimeException {
        public SalePeriodExpiredException(String message) { super(message); }
    }
    public static class InvalidPaymentException extends RuntimeException {
        public InvalidPaymentException(String message) { super(message); }
    }
}
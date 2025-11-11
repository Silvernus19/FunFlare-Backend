// src/main/java/com/funflare/funflare/service/PurchaseService.java
package com.funflare.funflare.service;

import com.funflare.funflare.dto.PurchaseCreateDTO;
import com.funflare.funflare.dto.PurchaseCreateDTO.TicketSelectDTO;
import com.funflare.funflare.model.*;
import com.funflare.funflare.model.Purchases.PaymentMethod;
import com.funflare.funflare.model.Purchases.Status;
import com.funflare.funflare.repository.*;
import com.funflare.funflare.util.PdfTicketGenerator;
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
import java.util.ArrayList;
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
    private final PointsService pointsService;
    private final EmailService emailService; // ← INJECTED

    @Autowired
    public PurchaseService(
            PurchasesRepository purchasesRepository,
            TicketPurchaseRepository ticketPurchaseRepository,
            TicketRepository ticketRepository,
            UserRepository userRepository,
            WalletRepository walletRepository,
            MpesaService mpesaService,
            EntityManager entityManager,
            PointsService pointsService,
            EmailService emailService // ← ADDED
    ) {
        this.purchasesRepository = purchasesRepository;
        this.ticketPurchaseRepository = ticketPurchaseRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.mpesaService = mpesaService;
        this.entityManager = entityManager;
        this.pointsService = pointsService;
        this.emailService = emailService;
    }

    @Transactional
    public Purchases createPurchase(PurchaseCreateDTO dto, Long userId) {
        logger.info("Creating purchase for user {} | eventId={} | method={}",
                userId, dto.getEventId(), dto.getPaymentMethod());

        User buyer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        validateTicketsAvailability(dto.getEventId(), dto.getSelectedTickets());

        BigDecimal totalAmount = dto.getSelectedTickets().stream()
                .map(t -> BigDecimal.valueOf(t.getQuantity()).multiply(t.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalQuantity = dto.getSelectedTickets().stream()
                .mapToInt(TicketSelectDTO::getQuantity)
                .sum();

        // 1. CREATE PURCHASE RECORD
        Purchases purchase = new Purchases();
        purchase.setUser(buyer);
        purchase.setQuantity(totalQuantity);
        purchase.setTotalAmount(totalAmount.doubleValue());
        purchase.setStatus(Status.PENDING);
        purchase.setPaymentMethod(PaymentMethod.valueOf(dto.getPaymentMethod().toUpperCase()));
        purchase.setPurchaseEmail(dto.getPurchaseEmail());
        purchase.setPhoneNumber(dto.getPaymentMethod().equalsIgnoreCase("MPESA") ? dto.getPhoneNumber() : null);
        purchase.setGuestName(dto.getGuestName());
        purchase.setPurchaseDate(OffsetDateTime.now(ZoneOffset.UTC));
        purchase = purchasesRepository.save(purchase);

        logger.info("Purchase record created | id={} | total={} KES", purchase.getId(), totalAmount);

        // 2. CREATE TICKET LINE ITEMS + QR + REDUCE STOCK
        createTicketLineItems(purchase, dto);

        // 3. PROCESS PAYMENT (SWITCH)
        try {
            processPayment(purchase, dto.getPaymentMethod(), buyer, totalAmount);
        } catch (Exception e) {
            purchase.setStatus(Status.CANCELLED);
            purchasesRepository.save(purchase);
            logger.error("Payment failed for purchase {} | method={}", purchase.getId(), dto.getPaymentMethod(), e);
            throw new RuntimeException("Payment failed: " + e.getMessage(), e);
        }

        // 4. SEND CONFIRMATION WITH PDF TICKETS
        sendPurchaseConfirmation(purchase, dto.getPurchaseEmail());

        logger.info("Purchase {} processed | method={} | final status={}",
                purchase.getId(), dto.getPaymentMethod(), purchase.getStatus());

        return purchase;
    }

    // ──────────────────────────────────────────────────────────────
    //  TICKET LINE ITEMS + QR + STOCK REDUCTION
    // ──────────────────────────────────────────────────────────────
    private void createTicketLineItems(Purchases purchase, PurchaseCreateDTO dto) {
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

                generateQrForTicket(tp);

                // Reduce stock with pessimistic lock
                Ticket lockedTicket = entityManager.find(Ticket.class, ticket.getId(), LockModeType.PESSIMISTIC_WRITE);
                if (lockedTicket.getQuantity() <= 0) {
                    throw new RuntimeException("Stock depleted for " + sel.getTicketType());
                }
                lockedTicket.setQuantity(lockedTicket.getQuantity() - 1);
                lockedTicket.setQuantitySold(lockedTicket.getQuantitySold() + 1);
                ticketRepository.save(lockedTicket);
            }
        }
    }

    // ──────────────────────────────────────────────────────────────
    //  PAYMENT SWITCH
    // ──────────────────────────────────────────────────────────────
    private void processPayment(Purchases purchase, String method, User buyer, BigDecimal totalAmount) {
        PaymentMethod pm = PaymentMethod.valueOf(method.toUpperCase());

        switch (pm) {
            case MPESA -> handleMpesa(purchase, buyer);
            case POINTS -> handlePoints(purchase, buyer, totalAmount);
            case WALLET -> {
                logger.warn("WALLET payment selected but not implemented | purchaseId={}", purchase.getId());
                throw new IllegalArgumentException("Wallet payment is not available yet");
            }
            default -> throw new IllegalArgumentException("Unsupported payment method: " + pm);
        }
    }

    // ──────────────────────────────────────────────────────────────
    //  MPESA – NO CHANGES
    // ──────────────────────────────────────────────────────────────
    private void handleMpesa(Purchases purchase, User buyer) {
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
                (int) Math.round(purchase.getTotalAmount()),
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

    // ──────────────────────────────────────────────────────────────
    //  POINTS – NO CHANGES
    // ──────────────────────────────────────────────────────────────
    private void handlePoints(Purchases purchase, User buyer, BigDecimal totalAmountKsh) {
        logger.info("Processing POINTS payment | purchaseId={} | amount={} KSH", purchase.getId(), totalAmountKsh);

        BigDecimal userPoints = pointsService.getUserPointsBalance(buyer.getId());
        BigDecimal requiredPoints = totalAmountKsh;

        if (userPoints.compareTo(requiredPoints) < 0) {
            throw new InvalidPaymentException(
                    String.format("Insufficient points: need %.0f, have %.0f", requiredPoints, userPoints));
        }

        pointsService.deductPoints(buyer.getId(), requiredPoints, purchase.getId());

        purchase.setStatus(Status.COMPLETED);
        purchase.setTransactionRef("POINTS-" + purchase.getId());
        purchasesRepository.save(purchase);

        logger.info("POINTS payment SUCCESS | deducted {} points | purchase {}", requiredPoints, purchase.getId());
    }

    // ──────────────────────────────────────────────────────────────
    //  QR GENERATION – NO CHANGES
    // ──────────────────────────────────────────────────────────────
    private void generateQrForTicket(TicketPurchase tp) {
        String qrText = tp.getQrCodeUid();
        try {
            byte[] png = QrCodeGenerator.generatePng(qrText, 400, 400);
            tp.setQrCodeImage(png);
            ticketPurchaseRepository.save(tp);
            logger.info("QR generated for ticket_purchase_id={} ({} bytes)", tp.getId(), png.length);
        } catch (WriterException | IOException e) {
            logger.error("QR generation failed for ticket_purchase_id={}", tp.getId(), e);
        }
    }

    // ──────────────────────────────────────────────────────────────
    //  EMAIL CONFIRMATION + PDF TICKETS (REAL EMAIL SENT)
    // ──────────────────────────────────────────────────────────────
    private void sendPurchaseConfirmation(Purchases purchase, String email) {
        List<TicketPurchase> tickets = ticketPurchaseRepository.findByPurchaseId(purchase.getId());

        List<byte[]> pdfAttachments = new ArrayList<>();
        List<String> pdfNames = new ArrayList<>();

        for (int i = 0; i < tickets.size(); i++) {
            TicketPurchase tp = tickets.get(i);
            try {
                byte[] pdfBytes = PdfTicketGenerator.generateTicketPdf(tp);
                pdfAttachments.add(pdfBytes);
                pdfNames.add("FunFlare_Ticket_" + (i + 1) + "_of_" + tickets.size() + ".pdf");
                logger.info("PDF generated: {} bytes | {}", pdfBytes.length, pdfNames.get(i));
            } catch (Exception e) {
                logger.error("PDF generation failed for ticket {}", tp.getId(), e);
            }
        }

        // SEND REAL EMAIL USING YOUR EXISTING EmailService
        emailService.sendTicketEmail(
                email,
                purchase.getGuestName(),
                purchase,
                pdfAttachments,
                pdfNames
        );

        logger.info("TICKET EMAIL SENT → {} | {} PDF(s) attached | Purchase #{}",
                email, pdfAttachments.size(), purchase.getId());
    }

    // ──────────────────────────────────────────────────────────────
    //  VALIDATION – NO CHANGES
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

    // ──────────────────────────────────────────────────────────────
    //  EXCEPTIONS
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
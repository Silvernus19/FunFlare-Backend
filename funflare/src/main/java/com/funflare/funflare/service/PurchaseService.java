package com.funflare.funflare.service;

import com.funflare.funflare.dto.PurchaseCreateDTO;
import com.funflare.funflare.dto.PurchaseCreateDTO.TicketSelectDTO;
import com.funflare.funflare.model.*;
import com.funflare.funflare.model.Purchases.PaymentMethod;
import com.funflare.funflare.model.Purchases.Status;
import com.funflare.funflare.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    public PurchaseService(
            PurchasesRepository purchasesRepository,
            TicketPurchaseRepository ticketPurchaseRepository,
            TicketRepository ticketRepository,
            UserRepository userRepository,
            WalletRepository walletRepository,
            MpesaService mpesaService,
            EntityManager entityManager
    ) {
        this.purchasesRepository = purchasesRepository;
        this.ticketPurchaseRepository = ticketPurchaseRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.mpesaService = mpesaService;
        this.entityManager = entityManager;
    }

    @Transactional
    public Purchases createPurchase(PurchaseCreateDTO dto, Long userId) {
        logger.info("Creating purchase for user {} on event {}", userId, dto.getEventId());

        User buyer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        // Validate availability before proceeding
        validateTicketsAvailability(dto.getEventId(), dto.getSelectedTickets());

        // Calculate total
        BigDecimal calculatedTotal = dto.getSelectedTickets().stream()
                .map(t -> BigDecimal.valueOf(t.getQuantity()).multiply(t.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalQuantity = dto.getSelectedTickets().stream()
                .mapToInt(TicketSelectDTO::getQuantity)
                .sum();

        // Create purchase
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

        // Create TicketPurchase line items + reduce stock
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

                // Pessimistic lock + reduce stock
                Ticket lockedTicket = entityManager.find(Ticket.class, ticket.getId(), LockModeType.PESSIMISTIC_WRITE);
                if (lockedTicket.getQuantity() <= 0) {
                    throw new RuntimeException("Stock depleted for " + sel.getTicketType());
                }
                lockedTicket.setQuantity(lockedTicket.getQuantity() - 1);
                lockedTicket.setQuantitySold(lockedTicket.getQuantitySold() + 1);
                ticketRepository.save(lockedTicket);
            }
        }

        // Process payment
        try {
            processPayment(purchase, dto.getPaymentMethod(), buyer);

            // Mark as COMPLETED only if not MPESA (instant payment)
            if (!PaymentMethod.MPESA.equals(purchase.getPaymentMethod())) {
                purchase.setStatus(Status.COMPLETED);
                purchasesRepository.save(purchase);
            }
        } catch (Exception e) {
            purchase.setStatus(Status.CANCELLED);
            purchasesRepository.save(purchase);
            throw new RuntimeException("Payment failed: " + e.getMessage(), e);
        }

        sendPurchaseConfirmation(purchase, dto.getPurchaseEmail());
        logger.info("Purchase {} created for {} tickets", purchase.getId(), totalQuantity);
        return purchase;
    }

    private void validateTicketsAvailability(Long eventId, List<TicketSelectDTO> selectedTickets) {
        ZoneOffset eat = ZoneOffset.of("+03:00");
        OffsetDateTime now = OffsetDateTime.now(eat);  // EAT TIME

        for (TicketSelectDTO sel : selectedTickets) {
            Ticket ticket = ticketRepository
                    .findByEventIdAndType(eventId, sel.getTicketTypeEnum())
                    .orElseThrow(() -> new RuntimeException("Ticket type not found: " + sel.getTicketType()));

            OffsetDateTime start = ticket.getSaleStartDate()
                    .atTime(ticket.getSaleStartTime())
                    .atOffset(eat);  // EAT

            OffsetDateTime end = ticket.getSaleEndDate()
                    .atTime(ticket.getSaleEndTime() != null ? ticket.getSaleEndTime() : LocalTime.MAX)
                    .atOffset(eat);  // EAT

            logger.info("Checking sale: {} | Now: {} | Start: {} | End: {}",
                    sel.getTicketType(), now, start, end);

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

        switch (pm) {
            case MPESA -> {
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

                // Extract CheckoutRequestID
                String checkoutId = result.substring(result.indexOf("Checkout ID: ") + 13).trim();
                purchase.setTransactionRef(checkoutId);
                purchasesRepository.save(purchase);

                logger.info("STK Push sent. Awaiting callback for Checkout ID: {}", checkoutId);
            }

            case WALLET -> {
                Wallet wallet = buyer.getWallet();
                if (wallet == null) throw new InvalidPaymentException("No wallet linked");
                if (wallet.getBalance() < amount) {
                    throw new InvalidPaymentException(
                            String.format("Insufficient wallet balance: %.2f < %.2f", wallet.getBalance(), amount)
                    );
                }
                wallet.setBalance(wallet.getBalance() - amount);
                walletRepository.save(wallet);
            }

            default -> throw new IllegalArgumentException("Unsupported payment method: " + method);
        }

        logger.info("Payment processed for purchase {} via {}", purchase.getId(), pm);
    }

    private void sendPurchaseConfirmation(Purchases purchase, String email) {
        logger.info("Email confirmation sent to {} for purchase {}", email, purchase.getId());
        // TODO: Implement real email with QR codes
    }

    // Custom Exceptions
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
package com.funflare.funflare.controller;

import com.funflare.funflare.dto.PaymentRequestDTO;
import com.funflare.funflare.dto.PaymentResponseDTO;
import com.funflare.funflare.model.Purchases;
import com.funflare.funflare.model.Purchases.Status;
import com.funflare.funflare.repository.PurchasesRepository;
import com.funflare.funflare.service.MpesaService;
import com.funflare.funflare.service.PointsService;
import com.google.gson.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private MpesaService mpesaService;

    @Autowired
    private PurchasesRepository purchasesRepository;

    @Autowired
    private PointsService pointsService; // ← NEW: For awarding points

    // ==================== STK PUSH (unchanged) ====================
    @PostMapping("/stkpush")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(@Valid @RequestBody PaymentRequestDTO request) {
        String accountRef = "TICKET-" + System.currentTimeMillis();
        String transactionDesc = "Event Ticket Payment";

        String result = mpesaService.initiateStkPush(
                request.getPhoneNumber(),
                request.getAmount(),
                accountRef,
                transactionDesc
        );

        if (result.startsWith("Error:")) {
            return ResponseEntity.badRequest()
                    .body(new PaymentResponseDTO("Payment failed: " + result.substring(7)));
        }

        String checkoutId = extractCheckoutId(result);
        return ResponseEntity.ok(
                new PaymentResponseDTO("STK Push sent! Check your phone.", checkoutId, accountRef)
        );
    }

    private String extractCheckoutId(String result) {
        try {
            int start = result.indexOf("Checkout ID: ") + 13;
            int end = result.indexOf(" ", start);
            if (end == -1) end = result.length();
            return result.substring(start, end).trim();
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    // ==================== M-PESA CALLBACK (UPDATED) ====================
    @PostMapping("/mpesa/callback")
    @Transactional
    public ResponseEntity<String> handleCallback(@RequestBody String callbackBody) {
        logger.info("M-PESA Callback Received: {}", callbackBody);

        JsonObject root;
        try {
            root = gson.fromJson(callbackBody, JsonObject.class);
        } catch (Exception e) {
            logger.error("Invalid JSON in callback", e);
            return ResponseEntity.badRequest().body("Invalid JSON");
        }

        JsonObject body = root.getAsJsonObject("Body");
        if (body == null || !body.has("stkCallback")) {
            logger.warn("Missing Body/stkCallback in payload");
            return ResponseEntity.badRequest().body("Invalid payload");
        }

        JsonObject stkCallback = body.getAsJsonObject("stkCallback");
        String checkoutId = stkCallback.get("CheckoutRequestID").getAsString();
        int resultCode = stkCallback.get("ResultCode").getAsInt();

        // Find purchase by CheckoutRequestID
        Optional<Purchases> optPurchase = purchasesRepository.findByTransactionRef(checkoutId);
        if (optPurchase.isEmpty()) {
            logger.warn("No purchase found for CheckoutRequestID: {}", checkoutId);
            return ResponseEntity.ok("OK");
        }

        Purchases purchase = optPurchase.get();

        // Prevent double-processing
        if (purchase.getStatus() == Status.COMPLETED) {
            logger.info("Purchase {} already COMPLETED – ignoring duplicate callback", purchase.getId());
            return ResponseEntity.ok("OK");
        }

        if (resultCode != 0) {
            // Payment FAILED
            String resultDesc = stkCallback.has("ResultDesc")
                    ? stkCallback.get("ResultDesc").getAsString()
                    : "Unknown error";
            purchase.setStatus(Status.CANCELLED);
            purchasesRepository.save(purchase);
            logger.warn("Purchase {} FAILED: {}", purchase.getId(), resultDesc);
            return ResponseEntity.ok("OK");
        }

        // === SUCCESS: Validate amount & award points ===
        BigDecimal paidAmount = extractPaidAmount(stkCallback);
        BigDecimal expectedAmount = BigDecimal.valueOf(purchase.getTotalAmount());

        if (paidAmount == null) {
            logger.warn("Could not extract paid amount from callback for purchase {}", purchase.getId());
        } else if (paidAmount.compareTo(expectedAmount) < 0) {
            logger.warn("Insufficient payment: paid {} KES, expected {} KES for purchase {}", paidAmount, expectedAmount, purchase.getId());
            purchase.setStatus(Status.PENDING);
            purchasesRepository.save(purchase);
            return ResponseEntity.ok("OK");
        }

        // Update receipt and status
        String receipt = extractMpesaReceipt(stkCallback);
        purchase.setTransactionRef(receipt); // Replace CheckoutID with receipt
        purchase.setStatus(Status.COMPLETED);
        purchasesRepository.save(purchase);

        // === AWARD POINTS (Only on success) ===
        pointsService.awardPointsForPurchase(purchase);

        logger.info("Purchase {} PAID successfully. Receipt: {}. Points awarded.", purchase.getId(), receipt);
        return ResponseEntity.ok("OK");
    }

    // Extract M-Pesa Receipt Number
    private String extractMpesaReceipt(JsonObject stkCallback) {
        try {
            JsonArray items = stkCallback.getAsJsonObject("CallbackMetadata")
                    .getAsJsonArray("Item");
            for (JsonElement elem : items) {
                JsonObject item = elem.getAsJsonObject();
                if ("MpesaReceiptNumber".equals(item.get("Name").getAsString())) {
                    return item.get("Value").getAsString();
                }
            }
        } catch (Exception e) {
            logger.debug("Failed to extract receipt", e);
        }
        return "UNKNOWN";
    }

    // Extract paid amount
    private BigDecimal extractPaidAmount(JsonObject stkCallback) {
        try {
            JsonArray items = stkCallback.getAsJsonObject("CallbackMetadata")
                    .getAsJsonArray("Item");
            for (JsonElement elem : items) {
                JsonObject item = elem.getAsJsonObject();
                if ("Amount".equals(item.get("Name").getAsString())) {
                    return item.get("Value").getAsBigDecimal();
                }
            }
        } catch (Exception e) {
            logger.debug("Failed to extract amount", e);
        }
        return null;
    }
}
package com.funflare.funflare.controller;

import com.funflare.funflare.dto.PaymentRequestDTO;
import com.funflare.funflare.dto.PaymentResponseDTO;
import com.funflare.funflare.model.Purchases;
import com.funflare.funflare.repository.PurchasesRepository;
import com.funflare.funflare.service.MpesaService;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private static final Gson gson = new Gson();

    @Autowired
    private MpesaService mpesaService;

    @Autowired
    private PurchasesRepository purchasesRepository;  // ← ADD THIS

    // ==================== STK PUSH ====================
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

    // ==================== M-PESA CALLBACK ====================
    @PostMapping("/mpesa/callback")
    public ResponseEntity<String> handleCallback(@RequestBody String callbackBody) {
        logger.info("M-PESA Callback Received: {}", callbackBody);

        try {
            JsonObject root = gson.fromJson(callbackBody, JsonObject.class);
            JsonObject stkCallback = root.getAsJsonObject("Body")
                    .getAsJsonObject("stkCallback");

            String checkoutId = stkCallback.get("CheckoutRequestID").getAsString();
            int resultCode = stkCallback.get("ResultCode").getAsInt();

            // Find purchase by CheckoutRequestID (stored in transaction_ref)
            Purchases purchase = purchasesRepository.findByTransactionRef(checkoutId)
                    .orElse(null);

            if (purchase == null) {
                logger.warn("No purchase found for CheckoutRequestID: {}", checkoutId);
                return ResponseEntity.ok("OK");
            }

            if (resultCode == 0) {
                // SUCCESS
                String receipt = extractMpesaReceipt(stkCallback);
                purchase.setStatus(Purchases.Status.COMPLETED);
                purchase.setTransactionRef(receipt);  // Update from CheckoutID → M-Pesa Receipt
                purchasesRepository.save(purchase);

                logger.info("Purchase {} PAID successfully. M-Pesa Receipt: {}", purchase.getId(), receipt);
            } else {
                // FAILED
                String resultDesc = stkCallback.has("ResultDesc")
                        ? stkCallback.get("ResultDesc").getAsString()
                        : "Unknown error";
                purchase.setStatus(Purchases.Status.CANCELLED);
                purchasesRepository.save(purchase);

                logger.warn("Purchase {} FAILED: {}", purchase.getId(), resultDesc);
            }

        } catch (Exception e) {
            logger.error("Error processing M-Pesa callback: ", e);
            return ResponseEntity.status(500).body("ERROR");
        }

        return ResponseEntity.ok("OK");
    }

    // Extract M-Pesa Receipt Number from CallbackMetadata
    private String extractMpesaReceipt(JsonObject stkCallback) {
        try {
            JsonArray items = stkCallback.getAsJsonObject("CallbackMetadata")
                    .getAsJsonArray("Item");

            for (int i = 0; i < items.size(); i++) {
                JsonObject item = items.get(i).getAsJsonObject();
                String name = item.get("Name").getAsString();
                if ("MpesaReceiptNumber".equals(name)) {
                    return item.get("Value").getAsString();
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to extract M-Pesa receipt", e);
        }
        return "UNKNOWN";
    }
}
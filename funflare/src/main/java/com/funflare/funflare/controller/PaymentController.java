package com.funflare.funflare.controller;

import com.funflare.funflare.dto.PaymentRequestDTO;
import com.funflare.funflare.dto.PaymentResponseDTO;
import com.funflare.funflare.service.MpesaService;
import com.google.gson.Gson;
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

    /**
     * Step 1: User initiates payment (matches your flow example).
     * POST /api/payments/stkpush
     * Body: { "phoneNumber": "254712345678", "amount": 100 }
     */
    @PostMapping("/stkpush")
    public ResponseEntity<PaymentResponseDTO> initiatePayment(@Valid @RequestBody PaymentRequestDTO request) {
        String accountRef = "INV" + System.currentTimeMillis(); // Auto-gen as in flow; or pass as param
        String transactionDesc = "Payment for order " + accountRef;

        String result = mpesaService.initiateStkPush(request.getPhoneNumber(), request.getAmount(), accountRef, transactionDesc);

        if (result.startsWith("Error:")) {
            return ResponseEntity.badRequest().body(new PaymentResponseDTO(result.substring(7))); // Trim "Error: "
        }

        // Extract ID (simple split or use regex/Gson if needed)
        String checkoutId = result.contains("Checkout ID: ") ? result.split("Checkout ID: ")[1] : "Unknown";
        return ResponseEntity.ok(new PaymentResponseDTO("Payment prompted on phone", checkoutId, accountRef));
    }

    /**
     * Step 5-6: M-Pesa callback (POST to /api/payments/mpesa/callback).
     * Parses body, updates status (stub: log + DB update).
     */
    @PostMapping("/mpesa/callback")
    public ResponseEntity<String> handleCallback(@RequestBody String callbackBody) {
        logger.info("Received callback: {}", callbackBody);
        try {
            JsonObject root = gson.fromJson(callbackBody, JsonObject.class);
            JsonObject stkCallback = root.getAsJsonObject("Body").getAsJsonObject("stkCallback");
            int resultCode = stkCallback.get("ResultCode").getAsInt();
            String checkoutId = stkCallback.get("CheckoutRequestID").getAsString();

            if (resultCode == 0) {
                // Success: Extract metadata (as in flow)
                JsonObject metadata = stkCallback.getAsJsonObject("CallbackMetadata");
                // ... (parse Item array for Amount, MpesaReceiptNumber, etc.)
                String receipt = "QAZ1234R56"; // Stub; parse from metadata
                logger.info("Payment SUCCESS for {}: Receipt {}", checkoutId, receipt);
                // TODO: Update DB - e.g., ticketRepository.updateStatus(checkoutId, "SUCCESS", receipt);
            } else {
                logger.warn("Payment FAILED for {}: {}", checkoutId, stkCallback.get("ResultDesc").getAsString());
                // TODO: Update DB to "FAILED"
            }
        } catch (Exception e) {
            logger.error("Callback parsing error: ", e);
        }
        return ResponseEntity.ok("OK"); // Always 200
    }
}
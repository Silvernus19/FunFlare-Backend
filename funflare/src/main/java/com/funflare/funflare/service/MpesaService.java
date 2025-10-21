package com.funflare.funflare.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.funflare.funflare.config.MpesaConfig;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
public class MpesaService {

    private static final Logger logger = LoggerFactory.getLogger(MpesaService.class);
    private static final Gson gson = new Gson();
    private final OkHttpClient client = new OkHttpClient();

    @Autowired
    private MpesaConfig mpesaConfig;

    /**
     * Generate OAuth token for M-Pesa API (uses GET as per Daraja docs).
     * Tokens expire in ~1 hour; consider caching with @Cacheable.
     */
    public String generateToken() throws IOException {
        String credentials = mpesaConfig.getConsumerKey() + ":" + mpesaConfig.getConsumerSecret();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        // GET request with no body (official Daraja method)
        Request request = new Request.Builder()
                .url(mpesaConfig.getTokenUrl())
                .header("Authorization", "Basic " + encodedCredentials)
                .get()  // Changed to GET
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() != null ? response.body().string() : "";  // Safe body read
            logger.info("Token Response - Code: {}, Body: {}", response.code(), responseBody);  // Key log line

            if (!response.isSuccessful()) {
                logger.error("Token generation failed (Code {}): {}", response.code(), responseBody);
                throw new IOException("Token generation failed (Code " + response.code() + "): " + responseBody);
            }

            JsonObject json = gson.fromJson(responseBody, JsonObject.class);
            if (json == null) {
                logger.error("Invalid JSON in token response: {}", responseBody);
                throw new IOException("Invalid token response (non-JSON): " + responseBody);
            }
            if (!json.has("access_token")) {
                logger.error("No access_token in response: {}", responseBody);
                throw new IOException("Missing access_token: " + responseBody);
            }
            String token = json.get("access_token").getAsString();
            logger.info("Token generated successfully (first 10 chars): {}", token.substring(0, Math.min(10, token.length())));
            return token;
        }
    }

    /**
     * Initiate STK Push for ticket payment.
     * @param phoneNumber Phone number in international format (e.g., "254712345678")
     * @param amount Ticket amount in KES (must be integer >= 1)
     * @param accountRef Unique ticket reference (e.g., "TICKET-123")
     * @param transactionDesc Description (e.g., "Payment for Event Ticket")
     * @return Checkout Request ID on success, or error message
     */
    public String initiateStkPush(String phoneNumber, int amount, String accountRef, String transactionDesc) {
        try {
            String token = generateToken();

            // Generate timestamp for password and payload (format: yyyyMMddHHmmss) - UTC
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String timestamp = ZonedDateTime.now(ZoneId.of("UTC")).format(formatter);
            logger.info("Generated timestamp: {}", timestamp);  // Log for verification

            // STK Push payload
            JsonObject payload = new JsonObject();
            payload.addProperty("BusinessShortCode", mpesaConfig.getBusinessShortcode());
            String rawPassword = mpesaConfig.getBusinessShortcode() + mpesaConfig.getPasskey() + timestamp;
            String encodedPassword = Base64.getEncoder().encodeToString(rawPassword.getBytes());
            logger.info("Raw password prefix: {}... (length: {})", rawPassword.substring(0, 20) + "...", rawPassword.length());  // Partial log for debug
            payload.addProperty("Password", encodedPassword);
            payload.addProperty("Timestamp", timestamp);
            payload.addProperty("TransactionType", "CustomerPayBillOnline");
            payload.addProperty("Amount", amount);
            payload.addProperty("PartyA", phoneNumber);
            payload.addProperty("PartyB", mpesaConfig.getBusinessShortcode());
            payload.addProperty("PhoneNumber", phoneNumber);
            payload.addProperty("CallBackURL", mpesaConfig.getCallbackUrl());
            payload.addProperty("AccountReference", accountRef);
            payload.addProperty("TransactionDesc", transactionDesc);

            String payloadJson = gson.toJson(payload);
            logger.info("STK Push Payload: {}", payloadJson);  // Full payload log

            RequestBody body = RequestBody.create(
                    payloadJson,
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(mpesaConfig.getStkPushUrl())
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body() != null ? response.body().string() : "";  // Safe read
                logger.info("STK Push Response - Code: {}, Body: {}", response.code(), responseBody);  // Added logging for STK
                if (!response.isSuccessful()) {
                    logger.error("STK Push failed (Code {}): {}", response.code(), responseBody);
                    return "Error: " + responseBody;
                }
                JsonObject json = gson.fromJson(responseBody, JsonObject.class);
                if (json == null) {
                    logger.error("Invalid JSON in STK response: {}", responseBody);
                    return "Error: Invalid STK response (non-JSON)";
                }
                // Flat response structure per Daraja docs
                String resultCode = json.has("ResponseCode") ? json.get("ResponseCode").getAsString() : "1";
                if ("0".equals(resultCode)) {
                    String checkoutId = extractCheckoutId(responseBody);
                    logger.info("STK Push initiated successfully: {}", responseBody);
                    return "Success: Payment prompted. Checkout ID: " + checkoutId;
                } else {
                    String errorMsg = json.has("CustomerMessage") ? json.get("CustomerMessage").getAsString() : "Unknown error";
                    return "Error: " + errorMsg;
                }
            }
        } catch (IOException e) {
            logger.error("STK Push error: ", e);
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Extract CheckoutRequestID from STK Push response JSON.
     */
    private String extractCheckoutId(String responseBody) {
        JsonObject json = gson.fromJson(responseBody, JsonObject.class);
        if (json == null) {
            logger.error("Null JSON in extractCheckoutId: {}", responseBody);
            return "Unknown";
        }
        if (json.has("CheckoutRequestID")) {
            return json.get("CheckoutRequestID").getAsString();
        }
        return "Unknown";
    }

    /**
     * Generate password: Base64(Shortcode + Passkey + Timestamp)
     */
    private String generatePassword(String timestamp) {
        String rawPassword = mpesaConfig.getBusinessShortcode() + mpesaConfig.getPasskey() + timestamp;
        return Base64.getEncoder().encodeToString(rawPassword.getBytes());
    }
}
package com.funflare.funflare.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL) // Omit null fields
public class PaymentResponseDTO {

    private String message;
    private String checkoutRequestId;
    private String accountReference;
    private String error; // For failures

    // Explicit default constructor (fixes IDE false positive)
    public PaymentResponseDTO() {}

    // Constructors
    public PaymentResponseDTO(String message, String checkoutRequestId, String accountReference) {
        this.message = message;
        this.checkoutRequestId = checkoutRequestId;
        this.accountReference = accountReference;
    }

    public PaymentResponseDTO(String error) {
        this.error = error;
    }

    // Getters/setters (unchanged)
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getCheckoutRequestId() { return checkoutRequestId; }
    public void setCheckoutRequestId(String checkoutRequestId) { this.checkoutRequestId = checkoutRequestId; }

    public String getAccountReference() { return accountReference; }
    public void setAccountReference(String accountReference) { this.accountReference = accountReference; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
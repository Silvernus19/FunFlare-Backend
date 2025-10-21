package com.funflare.funflare.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PaymentRequestDTO {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^254[17]\\d{8}$", message = "Phone must be in format 2547XXXXXXXX or 2541XXXXXXXX")
    private String phoneNumber;

    @Min(value = 1, message = "Amount must be at least 1 KES")
    private int amount;

    // Default constructor, getters, setters (or Lombok: @Data)
    public void PaymentRequest() {}

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
}
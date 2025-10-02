package com.funflare.funflare.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class WalletCreateDTO {

    @NotNull
    @Min(1)
    private Long userId;


    @NotBlank
    @Size(min = 4, max = 255)
    private String walletPin;

    // GETTERS AND SETTERS

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getWalletPin() {
        return walletPin;
    }

    public void setWalletPin(String walletPin) {
        this.walletPin = walletPin;
    }
}
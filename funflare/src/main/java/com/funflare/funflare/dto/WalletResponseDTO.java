package com.funflare.funflare.dto;

import com.funflare.funflare.model.Wallet;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class WalletResponseDTO  {

    private Long walletId;

    private Long userId;

    private BigDecimal balance;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

//    constructors

    public WalletResponseDTO(Wallet wallet) {
        this.walletId = wallet.getWalletId();
        this.userId = wallet.getUserId();
        this.balance = wallet.getBalance();
        this.createdAt = wallet.getCreatedAt();
        this.updatedAt = wallet.getUpdatedAt();
    }


    // GETTERS AND SETTERS

    public Long getWalletId() {
        return walletId;
    }

    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
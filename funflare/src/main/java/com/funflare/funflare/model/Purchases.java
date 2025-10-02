package com.funflare.funflare.model;


import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "purchases")
public class Purchases {

    public enum purchase_status{
        REFUNDED,
        CANCELLED,
        COMPLETED,
        PENDING


    }

    public enum payment_method {
        WALLET,
        POINTS,
        MPESA

    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private int purchaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "total_amount", nullable = false)
    private double totalAmount;

    @Column(name = "purchase_date")
    private OffsetDateTime purchaseDate;

    @Column(name = "status")
    private purchase_status status;

    @Column(name = "payment_method", nullable = false)
    private payment_method payment_method;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

//    getters and setters


    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public OffsetDateTime getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(OffsetDateTime purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public purchase_status getStatus() {
        return status;
    }

    public void setStatus(purchase_status status) {
        this.status = status;
    }

    public payment_method getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(payment_method payment_method) {
        this.payment_method = payment_method;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

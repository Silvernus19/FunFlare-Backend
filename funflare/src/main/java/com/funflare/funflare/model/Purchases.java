// src/main/java/com/funflare/funflare/model/Purchases.java
package com.funflare.funflare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a purchase transaction in the ticketing system.
 * Maps to the 'purchases' table.
 */
@Entity
@Table(name = "purchases")
public class Purchases {

    public enum Status {
        PENDING,
        COMPLETED,
        REFUNDED,
        CANCELLED
    }

    public enum PaymentMethod {
        MPESA,
        POINTS,
        WALLET
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    // FIXED: ONE-TO-MANY RELATIONSHIP WITH TICKET PURCHASES
    @OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TicketPurchase> ticketPurchases = new ArrayList<>();

    @Column(name = "quantity", nullable = false)
    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @Column(name = "total_amount", nullable = false)
    @NotNull
    private Double totalAmount;

    @Column(name = "purchase_date", nullable = false, updatable = false)
    @NotNull
    private OffsetDateTime purchaseDate = OffsetDateTime.now();

    @Column(name = "transaction_ref", length = 255)
    private String transactionRef;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull
    private Status status = Status.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 20)
    private PaymentMethod paymentMethod;

    @Column(name = "updated_at", nullable = false)
    @NotNull
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @Column(name = "purchase_email", nullable = false, length = 255)
    @NotNull
    private String purchaseEmail = "";

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "guest_name", length = 255)
    private String guestName;

    // Constructors
    public Purchases() {}

    public Purchases(User user, Integer quantity, Double totalAmount, PaymentMethod paymentMethod, String purchaseEmail) {
        this.user = user;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.purchaseEmail = purchaseEmail;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // GETTERS & SETTERS
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // FIXED: NOW WORKS IN EmailService
    public List<TicketPurchase> getTicketPurchases() {
        return ticketPurchases;
    }
    public void setTicketPurchases(List<TicketPurchase> ticketPurchases) {
        this.ticketPurchases = ticketPurchases;
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public OffsetDateTime getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(OffsetDateTime purchaseDate) { this.purchaseDate = purchaseDate; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getPurchaseEmail() { return purchaseEmail; }
    public void setPurchaseEmail(String purchaseEmail) { this.purchaseEmail = purchaseEmail; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
}
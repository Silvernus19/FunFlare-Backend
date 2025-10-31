package com.funflare.funflare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Entity representing an individual ticket purchase line item.
 * Maps to the 'ticket_purchases' table.
 * Each row is one bought ticket with its own QR code and status.
 */
@Entity
@Table(name = "ticket_purchases")
public class TicketPurchase {

    public enum Status {
        VALID,
        USED,
        CANCELLED,
        REFUNDED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_purchase_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    @NotNull
    private Purchases purchases;  // FK to purchases (cascade delete)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    @NotNull
    private Ticket ticket;  // FK to tickets (restrict delete)

    @Column(name = "ticket_price", nullable = false)
    @NotNull
    @DecimalMin(value = "0.0", message = "Ticket price cannot be negative")
    private Double ticketPrice;

    @Column(name = "guest_name", length = 255)
    @Size(max = 255, message = "Guest name too long")
    private String guestName;

    @Column(name = "guest_email", length = 255)
    @Size(max = 255, message = "Guest email too long")
    private String guestEmail;

    @Column(name = "guest_phone", length = 255)
    @Size(max = 255, message = "Guest phone too long")
    private String guestPhone;

    @Column(name = "qr_code_uid", nullable = false, unique = true, length = 255)
    @NotNull
    @Size(max = 255, message = "QR code UID too long")
    private String qrCodeUid = UUID.randomUUID().toString();  // Default unique UUID as string

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull
    private Status status = Status.VALID;

    @Column(name = "checked_in_at")
    private OffsetDateTime checkedInAt;

    @Column(name = "updated_at", nullable = false)
    @NotNull
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    // Constructors
    public TicketPurchase() {}

    public TicketPurchase(Purchases purchases, Ticket ticket, Double ticketPrice) {
        this.purchases = purchases;
        this.ticket = ticket;
        this.ticketPrice = ticketPrice;
    }

    // PreUpdate for updated_at
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Purchases getPurchase() {
        return purchases;
    }

    public void setPurchase(Purchases purchase) {
        this.purchases = purchase;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public Double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(Double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }

    public String getQrCodeUid() {
        return qrCodeUid;
    }

    public void setQrCodeUid(String qrCodeUid) {
        this.qrCodeUid = qrCodeUid;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public OffsetDateTime getCheckedInAt() {
        return checkedInAt;
    }

    public void setCheckedInAt(OffsetDateTime checkedInAt) {
        this.checkedInAt = checkedInAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
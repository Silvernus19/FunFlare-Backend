package com.funflare.funflare.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "ticket_purchases")
public class TicketPurchases {

    public enum ticket_status {
        USED,
        CANCELLED,
        REFUNDED
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticketpurchase_id")
    private int ticket_purchases_id;

    // Relationship to Purchase
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)  // FK to purchases
    private Purchases purchases;

    // Relationship to Ticket
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)   // FK to tickets
    private Ticket ticket;

    @Column(name = "ticket_price", nullable = false)
    private double ticket_price;

    @Column(name ="guest_name")
    private String guest_name;

    @Column(name = "guest_phone")
    private String guest_phone;

    @Column(name = "qr_code_uid", nullable = false)
    private String qr_code_uid;

    @Column(name = "status", nullable = false)
    private ticket_status status;

    @Column(name = "checked_in_at")
    private Date checked_in_at;

    @Column(name ="updated_at")
    private Date updated_at;

//    getters and setters


    public int getTicket_purchases_id() {
        return ticket_purchases_id;
    }

    public void setTicket_purchases_id(int ticket_purchases_id) {
        this.ticket_purchases_id = ticket_purchases_id;
    }

    public Purchases getPurchases() {
        return purchases;
    }

    public void setPurchases(Purchases purchases) {
        this.purchases = purchases;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public double getTicket_price() {
        return ticket_price;
    }

    public void setTicket_price(double ticket_price) {
        this.ticket_price = ticket_price;
    }

    public String getGuest_name() {
        return guest_name;
    }

    public void setGuest_name(String guest_name) {
        this.guest_name = guest_name;
    }

    public String getGuest_phone() {
        return guest_phone;
    }

    public void setGuest_phone(String guest_phone) {
        this.guest_phone = guest_phone;
    }

    public String getQr_code_uid() {
        return qr_code_uid;
    }

    public void setQr_code_uid(String qr_code_uid) {
        this.qr_code_uid = qr_code_uid;
    }

    public ticket_status getStatus() {
        return status;
    }

    public void setStatus(ticket_status status) {
        this.status = status;
    }

    public Date getChecked_in_at() {
        return checked_in_at;
    }

    public void setChecked_in_at(Date checked_in_at) {
        this.checked_in_at = checked_in_at;
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = updated_at;
    }
}

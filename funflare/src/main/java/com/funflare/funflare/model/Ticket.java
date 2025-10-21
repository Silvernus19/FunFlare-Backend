package com.funflare.funflare.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    public enum Type {
        EARLY_BIRD,
        ADVANCE,
        VIP
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_type", nullable = false)
    private Type type;

    @Column(name = "ticket_price")
    private BigDecimal price;

    @Column(name = "ticket_quantity")
    private Integer quantity;

    @Column(name = "quantity_sold")
    private Integer quantitySold;

    @Column(name = "ticket_metadata")
    private String metadata;

    @Column(name = "ticket_created_at")
    private OffsetDateTime createdAt;

    @Column(name = "ticket_updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "ticket_sale_start_date")
    private LocalDate saleStartDate;

    @Column(name = "ticket_sale_end_date")
    private LocalDate saleEndDate;

    @Column(name = "ticket_sale_start_time")
    private LocalTime saleStartTime;

    @Column(name = "ticket_sale_end_time")
    private LocalTime saleEndTime;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(Integer quantitySold) {
        this.quantitySold = quantitySold;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
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

    public LocalDate getSaleStartDate() {
        return saleStartDate;
    }

    public void setSaleStartDate(LocalDate saleStartDate) {
        this.saleStartDate = saleStartDate;
    }

    public LocalDate getSaleEndDate() {
        return saleEndDate;
    }

    public void setSaleEndDate(LocalDate saleEndDate) {
        this.saleEndDate = saleEndDate;
    }

    public LocalTime getSaleStartTime() {
        return saleStartTime;
    }

    public void setSaleStartTime(LocalTime saleStartTime) {
        this.saleStartTime = saleStartTime;
    }

    public LocalTime getSaleEndTime() {
        return saleEndTime;
    }

    public void setSaleEndTime(LocalTime saleEndTime) {
        this.saleEndTime = saleEndTime;
    }
}
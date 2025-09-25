package com.funflare.funflare.dto;

import com.funflare.funflare.model.Ticket;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

public class TicketResponseDTO {

    private Long id;
    private String eventName;
    private String type;
    private BigDecimal price;
    private Integer quantity;
    private Integer quantitySold;
    private String metadata;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private LocalDate saleStartDate;
    private LocalDate saleEndDate;
    private LocalTime saleStartTime;
    private LocalTime saleEndTime;

    // Constructor to map from Ticket entity
    public TicketResponseDTO(Ticket ticket) {
        this.id = ticket.getId();
        this.type = ticket.getType() != null ? ticket.getType().name() : null;
        this.price = ticket.getPrice();
        this.quantity = ticket.getQuantity();
        this.quantitySold = ticket.getQuantitySold();
        this.metadata = ticket.getMetadata();
        this.createdAt = ticket.getCreatedAt();
        this.updatedAt = ticket.getUpdatedAt();
        this.saleStartDate = ticket.getSaleStartDate();
        this.saleEndDate = ticket.getSaleEndDate();
        this.saleStartTime = ticket.getSaleStartTime();
        this.saleEndTime = ticket.getSaleEndTime();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
package com.funflare.funflare.dto;

import com.funflare.funflare.model.Event;
import com.funflare.funflare.model.Ticket;

import java.security.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;

public class TicketResponseDTO {



    private long  id;
    private String event_name;
    private String type;
    private double price;
    private Integer quantity;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private LocalDate saleStartDate;
    private LocalDate saleEndDate;
    private LocalTime saleStartTime;
    private LocalTime saleEndTime;

    public TicketResponseDTO(Ticket ticket) {
        this.id = ticket.getId();
        this.event_name = ticket.getEvent().getName();
        this.type = ticket.getType().name();
        this.price = ticket.getPrice();
        this.quantity = ticket.getQuantity();
        this.createdAt = ticket.getCreatedAt();
        this.updatedAt = ticket.getUpdatedAt();
        this.saleStartDate = ticket.getSaleStartDate();
        this.saleEndDate = ticket.getSaleEndDate();
        this.saleStartTime = ticket.getSaleStartTime();
        this.saleEndTime = ticket.getSaleEndTime();

    }

//    getters and setters




    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public OffsetDateTime getCreated_at() {
        return createdAt;
    }

    public void setCreated_at(OffsetDateTime created_at) {
        this.createdAt = created_at;
    }

    public OffsetDateTime getUpdated_at() {
        return updatedAt;
    }

    public void setUpdated_at(OffsetDateTime updated_at) {
        this.updatedAt = updated_at;
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

package com.funflare.funflare.dto;

import com.funflare.funflare.model.Ticket;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class TicketCreateDTO {

    @NotNull(message = "Event ID cannot be null")
    private Long eventId;

    @NotBlank(message = "Ticket type cannot be blank")
    @Pattern(regexp = "(?i)(EARLY_BIRD|EARLYBIRD|ADVANCE)", message = "Ticket type must be EARLY_BIRD, EARLYBIRD, or ADVANCE")
    private String type;

    @NotNull(message = "Ticket price cannot be empty")
    private BigDecimal price;

    @NotNull(message = "Ticket quantity must not be null")
    @Min(value = 1, message = "Ticket quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Ticket sale start date cannot be blank")
    private LocalDate saleStartDate;

    @NotNull(message = "Ticket sale end date cannot be null")
    private LocalDate saleEndDate;

    @NotNull(message = "Ticket sale start time cannot be null")
    private LocalTime saleStartTime;

    @NotNull(message = "Ticket sale end time cannot be null")
    private LocalTime saleEndTime;

    // Getters and setters
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
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
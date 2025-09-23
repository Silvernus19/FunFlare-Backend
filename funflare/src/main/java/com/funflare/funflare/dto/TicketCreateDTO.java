package com.funflare.funflare.dto;

import com.funflare.funflare.model.Ticket;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public class TicketCreateDTO {

@NotBlank(message = "ticket type cannot be blank")
    private String type;
@NotNull(message = "ticket price cannot be empty")
    private Double price;
@NotNull(message = "ticket quantity must not be null")
    private Integer quantity;
@NotNull(message = "ticket sale start date cannot be blank")
    private LocalDate saleStartDate;
@NotNull(message = "ticket sale end date cannot be null")
    private LocalDate saleEndDate;
@NotNull(message = "ticket sale start time cannot eb null")
    private LocalTime saleStartTime;
@NotNull(message = "ticket sale end time cannot be null")
    private LocalTime saleEndTime;


//Getters and setters


    public LocalTime getSaleStartTime() {
        return saleStartTime;
    }

    public void setSaleStartTime(LocalTime saleStartTime) {
        this.saleStartTime = saleStartTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public LocalTime getSaleEndTime() {
        return saleEndTime;
    }

    public void setSaleEndTime(LocalTime saleEndTime) {
        this.saleEndTime = saleEndTime;
    }
}

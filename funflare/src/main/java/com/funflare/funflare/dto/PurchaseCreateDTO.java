// src/main/java/com/funflare/funflare/dto/PurchaseCreateDTO.java
package com.funflare.funflare.dto;

import com.funflare.funflare.model.Ticket;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class PurchaseCreateDTO {

    @NotNull(message = "Event ID is required")
    @Min(value = 1, message = "Event ID must be positive")
    private Long eventId;

    @Valid
    @NotEmpty(message = "At least one ticket type must be selected")
    @Size(min = 1, message = "You must select at least one ticket")
    private List<TicketSelectDTO> selectedTickets;

    @NotNull(message = "Payment method is required")
    @Pattern(regexp = "MPESA", message = "Only MPESA is supported")
    private String paymentMethod = "MPESA";
//
//    @NotNull(message = "Email is required")
    @Email(message = "Please enter a valid email")
    @Size(max = 255, message = "Email too long")
    private String purchaseEmail;

    // FIXED: REMOVED @NotBlank — IT WAS KILLING YOUR PHONE VALIDATION
//    @NotNull(message = "Phone number is required")
    @Pattern(
            regexp = "^254[71][0-9]{8}$",
            message = "Phone must be in format 2547XXXXXXXXX (Kenyan MPESA number)"
    )
    @Size(min = 12, max = 12, message = "Phone must be exactly 12 digits starting with 254")
    private String phoneNumber;

//    @NotNull(message = "Name is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String guestName;

    // NESTED DTO
    public static class TicketSelectDTO {

        @NotBlank(message = "Ticket type is required")
        @Pattern(
                regexp = "^(EARLY_BIRD|ADVANCE|REGULAR|VIP|VVIP|PLATINUM)$",
                message = "Invalid ticket type"
        )
        private String ticketType;

        @NotNull(message = "Quantity is required")
        @Min(value = 1)
        @Max(value = 50)
        private Integer quantity;

        @NotNull(message = "Price is required")
        @Digits(integer = 10, fraction = 2)
        private BigDecimal price;

        public Ticket.Type getTicketTypeEnum() {
            if (ticketType == null || ticketType.isBlank()) {
                throw new IllegalArgumentException("Ticket type cannot be null or empty");
            }
            try {
                return Ticket.Type.valueOf(ticketType.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Invalid ticket type: '" + ticketType + "'. Valid: EARLY_BIRD, ADVANCE, REGULAR, VIP, VVIP, PLATINUM"
                );
            }
        }

        // Getters & Setters
        public String getTicketType() { return ticketType; }
        public void setTicketType(String ticketType) { this.ticketType = ticketType; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

    // GETTERS & SETTERS
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public List<TicketSelectDTO> getSelectedTickets() { return selectedTickets; }
    public void setSelectedTickets(List<TicketSelectDTO> selectedTickets) { this.selectedTickets = selectedTickets; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPurchaseEmail() { return purchaseEmail; }
    public void setPurchaseEmail(String purchaseEmail) { this.purchaseEmail = purchaseEmail; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
}
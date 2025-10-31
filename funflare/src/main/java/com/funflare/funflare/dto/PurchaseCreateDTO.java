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
    private List<TicketSelectDTO> selectedTickets;

    @NotNull(message = "Payment method is required")
    @NotBlank(message = "Payment method cannot be empty")
    @Size(max = 20, message = "Payment method too long")
    private String paymentMethod;

    @NotNull(message = "Purchase email is required")
    @NotBlank(message = "Purchase email cannot be empty")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email too long")
    private String purchaseEmail;

    @Size(max = 20, message = "Phone number too long")
    private String phoneNumber;

    @Size(max = 255, message = "Guest name too long")
    private String guestName;

    // NESTED DTO
    public static class TicketSelectDTO {
        @NotNull(message = "Ticket type is required")
        @NotBlank(message = "Ticket type cannot be empty")
        @Size(max = 20, message = "Ticket type too long")
        private String ticketType;  // "EARLY_BIRD", "VIP"

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        @NotNull(message = "Price per ticket is required")
        @DecimalMin(value = "0.00", message = "Price cannot be negative")
        private BigDecimal price;

        // CONVERT String → Enum
        public Ticket.Type getTicketTypeEnum() {
            if (ticketType == null || ticketType.isBlank()) {
                throw new IllegalArgumentException("Ticket type is required");
            }
            try {
                return Ticket.Type.valueOf(ticketType.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid ticket type: " + ticketType + ". Use: EARLY_BIRD, ADVANCE, VIP");
            }
        }

        // Standard getters/setters
        public String getTicketType() { return ticketType; }
        public void setTicketType(String ticketType) { this.ticketType = ticketType; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

    // Getters and Setters
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
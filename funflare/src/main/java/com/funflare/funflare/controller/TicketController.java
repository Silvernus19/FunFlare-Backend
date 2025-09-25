package com.funflare.funflare.controller;

import com.funflare.funflare.dto.TicketCreateDTO;
import com.funflare.funflare.dto.TicketResponseDTO;
import com.funflare.funflare.model.Ticket;
import com.funflare.funflare.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class TicketController {
    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/generate/tickets")
    public ResponseEntity<TicketResponseDTO> generateTickets(@Valid @RequestBody TicketCreateDTO dto) {
        Ticket ticket = ticketService.generateTickets(dto);
        TicketResponseDTO response = new TicketResponseDTO(ticket); // Convert to DTO
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
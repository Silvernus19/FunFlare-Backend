package com.funflare.funflare.service;


import com.funflare.funflare.dto.TicketCreateDTO;
import com.funflare.funflare.model.Event;
import com.funflare.funflare.model.Ticket;
import com.funflare.funflare.repository.EventRepository;
import com.funflare.funflare.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    private final EventService eventService;
    private TicketRepository ticketRepository;
    private EventRepository eventRepository;
    private Event event;
    private Ticket ticket;

    public TicketService(EventService eventService,  TicketRepository ticketRepository, EventRepository eventRepository) {
        this.eventService = eventService;
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.event = new Event();
    }

//    public Integer getTicketQuantity (Long TicketId) {
//        Ticket ticket = ticketRepository.findById(TicketId)
//        .orElseThrow(() -> new RuntimeException("Ticket Not Found"));
//        return ticket.getQuantity();
//    }


    @Transactional
                public Ticket generateTickets (TicketCreateDTO dto, Long eventId, Long quantity) {

//        check that tickets cannot be generated for cancelled or ended events
        if (event.getEventStatus() == Event.EventStatus.CANCELED || event.getEventStatus() == Event.EventStatus.DELETED) {
            throw new IllegalStateException("Cannot generate tickets for cancelled or ended events.");
        }

//        check that tickets cannot be regenerated for the same active event
        boolean ticketExists = ticketRepository.existsByEventId(eventId);
        if (event.getEventStatus() == Event.EventStatus.ACTIVE && ticketExists) {
            throw new IllegalStateException("Cannot generate tickets for the same active events.");
        }


        //check if the capacity is less than 1
        Integer capacity = eventService.getCapacity(eventId);
            if (capacity <= 1) {
                throw new IllegalArgumentException("Capacity cannot be less than 1");
            }

        //check that ticket quantity does not exceed the event capacity

                    if (dto.getQuantity() > capacity) {
                        throw new IllegalArgumentException(" Ticket Quantity cannot be greater than event capacity");
                    }

//    create a new ticket entity
        Ticket ticket = new Ticket();
        ticket.setEvent(event);
        ticket.setPrice(dto.getPrice());
        ticket.setQuantity(dto.getQuantity());
        ticket.setQuantitySold(0); // always start at zero

        // Convert string type into enum
        ticket.setType(Ticket.Type.valueOf(dto.getType().toUpperCase()));

        // Dates and times
        ticket.setSaleStartDate(dto.getSaleStartDate());
        ticket.setSaleEndDate(dto.getSaleEndDate());
        ticket.setSaleStartTime(dto.getSaleStartTime());
        ticket.setSaleEndTime(dto.getSaleEndTime());









        return ticketRepository.save(ticket);
    }

}

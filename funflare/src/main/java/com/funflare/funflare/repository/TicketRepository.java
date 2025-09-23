package com.funflare.funflare.repository;

import com.funflare.funflare.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TicketRepository  extends JpaRepository<Ticket,Long> {
    @Override
    Optional<Ticket> findById(Long TicketId);
    boolean existsByEventName(String eventName);
    //Optional<Ticket> findByname(String eventName);
    boolean existsByEventId(Long eventId);

}

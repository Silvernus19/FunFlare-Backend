package com.funflare.funflare.repository;

import com.funflare.funflare.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Integer> {

    boolean existsByEventStartTimeAndLocationAndEventStartDate(LocalTime startTime, String location, LocalDate eventStartDate);

    //@Override
    Optional<Event> findById(Long eventId);
    boolean existsByName(String eventName);

    List<Event> findByOrganizerId(Long userId);

    List<Event> findByEventStatusAndEventStartDateAfter(Event.EventStatus eventStatus, LocalDate localDate);
}

package com.funflare.funflare.repository;

import com.funflare.funflare.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.LocalTime;

public interface EventRepository extends JpaRepository<Event, Integer> {

    boolean existsByEventStartTimeAndLocationAndEventStartDate(LocalTime startTime, String location, LocalDate eventStartDate);
}

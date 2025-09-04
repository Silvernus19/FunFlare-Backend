package com.funflare.funflare.repository;

import com.funflare.funflare.model.Events;
import org.springframework.data.jpa.repository.JpaRepository;

public  interface EventRepository extends JpaRepository<Events, Integer> {

    boolean existsEventByid(Integer eventId);
    boolean existsEventByname(String eventName);

}
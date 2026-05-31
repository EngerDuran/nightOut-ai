package com.ironhack.nightoutai.repository;

import com.ironhack.nightoutai.model.Event;
import com.ironhack.nightoutai.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Optional<Event> findByName(String name);
    Optional<Event> findById(Long id);

}

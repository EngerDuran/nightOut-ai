package com.ironhack.nightoutai.repository;

import com.ironhack.nightoutai.model.Event;
import com.ironhack.nightoutai.model.Venue;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Implementación de Lock pesimista (PESSIMISTIC_WRITE) para garantizar consistencia en transacciones
    //También para prevenir condiciones de carrera en la asignación de plazas.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :id")
    Optional<Event> findByIdForUpdate(Long id);

    Optional<Event> findByName(String name);
    Optional<Event> findById(Long id);
    List<Event> findByVenue(Venue venue);

}

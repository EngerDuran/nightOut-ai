package com.ironhack.nightoutai.controller;


import com.ironhack.nightoutai.dto.EventRequestDTO;
import com.ironhack.nightoutai.model.Event;
import com.ironhack.nightoutai.model.Venue;
import com.ironhack.nightoutai.service.EventService;
import com.ironhack.nightoutai.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;
    private final VenueService venueService;

    @PostMapping
    public ResponseEntity<Event> addEvent(@RequestBody EventRequestDTO dto) {
        //Buscamos el Venue en la base de datos usando el ID
        Venue venue = venueService.findById(dto.getVenueId());

        //Instanciamos la entidad
        Event event = new Event();

        //Mapeamos los campos
        event.setName(dto.getName());
        event.setDate(dto.getDate());
        event.setStatus(dto.getStatus());
        event.setVenue(venue);

        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.addEvent(event));
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAll() {
        return ResponseEntity.ok(eventService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getById(@PathVariable Long id) {

        Event event = eventService.findById(id);

        return ResponseEntity.ok(event); // Devuelve un 200 con la lista

    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        // Validamos que el ID coincida
        if (!event.getId().equals(id)) {
            return ResponseEntity.badRequest().build();
        }

        //Delegamos la actualización al servicio
        Event updatedEvent = eventService.updateEvent(id, event);

        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok().build();
    }
}

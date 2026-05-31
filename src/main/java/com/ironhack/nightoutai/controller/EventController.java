package com.ironhack.nightoutai.controller;


import com.ironhack.nightoutai.model.Event;
import com.ironhack.nightoutai.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Event> addEvent(@RequestBody Event event) {
        new ResponseEntity<>(eventService.addEvent(event), HttpStatus.CREATED);
        return ResponseEntity.ok(event);
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

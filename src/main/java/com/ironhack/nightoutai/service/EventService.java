package com.ironhack.nightoutai.service;

import com.ironhack.nightoutai.model.Event;
import com.ironhack.nightoutai.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    public Event addEvent(Event event) {
        if (event.getDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event date cannot be in the past");
        }
       return eventRepository.save(event);
    }

    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    public Event findById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    public Event findByName(String name) {
        return eventRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found"));
    }

    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "this event not exist ");
        }
        eventRepository.deleteById(id);
    }

    public Event updateEvent(Long id, Event event) {

       // Buscamos si el evento existe
        Event existingEvent = findById(id);

        validateEventDate(event.getDate());

        existingEvent.setName(event.getName());
        existingEvent.setDate(event.getDate());
        existingEvent.setStatus(event.getStatus());
        existingEvent.setVenue(event.getVenue());

        return eventRepository.save(existingEvent);
    }

    private void validateEventDate(LocalDateTime date) {
        if (date.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event date cannot be in the past");
        }
    }
}

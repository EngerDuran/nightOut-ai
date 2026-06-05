package com.ironhack.nightoutai.tools;

import com.ironhack.nightoutai.model.Event;
import com.ironhack.nightoutai.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventTools {

    private final EventRepository eventRepository;

    // Metodo sin parámetros, no necesita @ToolParam
    @Tool(description = "It searches and returns absolutely all events available in the application's catalog.")
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    // Metodo con parámetro, usamos @ToolParam para describir dato
    @Tool(description = "Search and return a list of available events by filtering by a specific city or location.")
    public List<Event> getEventsByLocation(
            @ToolParam(description = "The name of the city or location where the user wants to search for events. Example: 'Madrid'")
            String location) {

        return eventRepository.findByVenueLocationIgnoreCase(location);
    }
}
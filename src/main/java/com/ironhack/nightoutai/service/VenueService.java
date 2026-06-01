package com.ironhack.nightoutai.service;

import com.ironhack.nightoutai.dto.VenueRequestDTO;
import com.ironhack.nightoutai.model.Venue;
import com.ironhack.nightoutai.repository.EventRepository;
import com.ironhack.nightoutai.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;

    public Venue addVenue(Venue venue) {
        return venueRepository.save(venue);
    }

    public List<Venue> getAll() {
        return venueRepository.findAll();
    }

    public Venue findById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));
    }

    public List<Venue> findByLocation(String location) {
        // 1. Buscamos en el repositorio
        List<Venue> venues = venueRepository.findByLocation(location);

        // 2. Validamos
        if (venues.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No venues found at this location: " + location);
        }

        // 3. Si hay resultados, los devolvemos
        return venues;
    }

    public Venue findByName(String name) {
        return venueRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found"));
    }

    public void deleteVenue(Long id) {
        if (!venueRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Venue not found");
        }
        venueRepository.deleteById(id);
    }

    public Venue updateVenue(Long id, VenueRequestDTO dto) {

        //Buscamos si el venue existe
        Venue existingVenue = findById(id);

        existingVenue.setName(dto.getName());
        existingVenue.setLocation(dto.getLocation());
        existingVenue.setCapacity(dto.getCapacity());
        return venueRepository.save(existingVenue);
    }

    public void validateCapacity(Venue venue, int newParticipants) {
        //Obtenemos capacidad máxima del venue
        int maxCapacity = venue.getCapacity();
        //Obtenemos cuantos están ocupando el venue
        int currentOcupancy = calculateCurrentOcupancy(venue);

        if (currentOcupancy + newParticipants > maxCapacity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La sala está llena");
        }
    }

    private int calculateCurrentOcupancy(Venue venue) {
        // buscamos todos los eventos de este venue (Hacer metodo en eventrepo luego)
        // y sumar la cantidad de personas
        return eventRepository.findByVenue(venue).size();
    }

}

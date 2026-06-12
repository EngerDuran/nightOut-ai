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

        if (venues.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No venues found at this location: " + location);
        }

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

        Venue existingVenue = findById(id);

        existingVenue.setName(dto.getName());
        existingVenue.setLocation(dto.getLocation());
        existingVenue.setCapacity(dto.getCapacity());
        return venueRepository.save(existingVenue);
    }

    public void validateCapacity(Venue venue, int newParticipants) {

        int maxCapacity = venue.getCapacity();

        int currentOcupancy = calculateCurrentOcupancy(venue);

        if (currentOcupancy + newParticipants > maxCapacity) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La sala está llena");
        }
    }

    private int calculateCurrentOcupancy(Venue venue) {

        return eventRepository.findByVenue(venue).size();
    }

}

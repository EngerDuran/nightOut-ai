package com.ironhack.nightoutai.controller;

import com.ironhack.nightoutai.dto.VenueRequestDTO;
import com.ironhack.nightoutai.model.Venue;
import com.ironhack.nightoutai.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {
    private final VenueService venueService;

    @PostMapping
    public ResponseEntity<Venue> addVenue(@RequestBody VenueRequestDTO dto) {

        //Instanciamos la entidad
        Venue venue = new Venue();


        venue.setName(dto.getName());
        venue.setLocation(dto.getLocation());
        venue.setCapacity(dto.getCapacity());

        //Delegamos la creación al servicio
        return ResponseEntity.status(HttpStatus.CREATED).body(venueService.addVenue(venue));
    }

    @GetMapping
    public ResponseEntity<List<Venue>> getAll() {
        return ResponseEntity.ok(venueService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venue> getById(@PathVariable Long id) {

        Venue venue = venueService.findById(id);

        return ResponseEntity.ok(venue);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venue> updateVenue(@PathVariable Long id, @RequestBody VenueRequestDTO dto) {
        // Delegamos la actualización al servicio directamente
        return ResponseEntity.ok(venueService.updateVenue(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable Long id) {
        venueService.deleteVenue(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/location/{location}")
   public ResponseEntity<List<Venue>> getVenuesByLocation(@PathVariable String location) {
        return ResponseEntity.ok(venueService.findByLocation(location));
    }

}

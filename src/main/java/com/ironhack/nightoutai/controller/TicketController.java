package com.ironhack.nightoutai.controller;

import com.ironhack.nightoutai.dto.TicketRequestDTO;
import com.ironhack.nightoutai.model.Ticket;
import com.ironhack.nightoutai.service.EventService;
import com.ironhack.nightoutai.service.TicketService;
import com.ironhack.nightoutai.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    // Servicios inyectados necesarios para realizar las operaciones
    private final TicketService ticketService;
    private final EventService eventService;
    private final VenueService venueService; // necesario para resolver el Evento desde el ID


    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody TicketRequestDTO dto) {
        // El servicio ya se encarga de la lógica, transaccionalidad y las excepciones.
        Ticket createdTicket = ticketService.addTicket(dto);


        return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
    }


    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long id, @RequestBody TicketRequestDTO dto ) {
        return ResponseEntity.ok(ticketService.updateTicket(id, dto));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }
}
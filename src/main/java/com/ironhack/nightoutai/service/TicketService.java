package com.ironhack.nightoutai.service;

import com.ironhack.nightoutai.dto.TicketRequestDTO;
import com.ironhack.nightoutai.model.Event;
import com.ironhack.nightoutai.model.Ticket;
import com.ironhack.nightoutai.repository.EventRepository;
import com.ironhack.nightoutai.repository.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;


@Service
@RequiredArgsConstructor
public class TicketService {
    private final TicketRepository ticketRepository;
    private final EventService eventService;
    private final EventRepository eventRepository;

    @Transactional
    public Ticket addTicket(TicketRequestDTO dto) {
    //Bloqueamos el evento(Hasta que el metodo termine nadie lo usa)
    Event event = eventRepository.findByIdForUpdate(dto.getEventId())
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "El evento no encontrado"));

        // Validación de fecha
        if (event.getDate().isBefore(java.time.LocalDateTime.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "No se puede comprar entradas para eventos pasados");
        }

        //Validaciones de negocio
        if (event.getAvailableSeats () <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "No hay cupos disponibles");
        }

        //Incrementamos el contador de cupos
        event.setSoldTickets(event.getSoldTickets() + 1);
        eventRepository.save(event);

        //Creamos el ticket
        Ticket ticket = new Ticket();
        ticket.setPrice(dto.getPrice());
        ticket.setEvent(event);

        //Retornamos el ticket guardado
        return ticketRepository.save(ticket);

    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }


    public Ticket findById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ticket no encontrado"));
    }

    public void deleteTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El evento no encontrado"));

        Event event = ticket.getEvent();

        //Decrementamos el contador de cupos
        event.setSoldTickets(event.getSoldTickets() - 1);
        eventRepository.save(event);
        ticketRepository.delete(ticket);
    }

    //Primero verifica si el ticket existe, luego guarda la versión actualizada.
    public Ticket updateTicket(Long id, TicketRequestDTO dto) {

        //miramos si el ticket existe
        Ticket existingTicket = findById(id);

       //Buscamos el evento real usando el ID que viene en el DTO
        Event event = eventService.findById(dto.getEventId());


        existingTicket.setPrice(dto.getPrice());
        existingTicket.setEvent(event);

        return ticketRepository.save(existingTicket);
    }
}
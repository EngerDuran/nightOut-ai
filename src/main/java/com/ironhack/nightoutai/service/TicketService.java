package com.ironhack.nightoutai.service;

import com.ironhack.nightoutai.dto.TicketRequestDto;
import com.ironhack.nightoutai.model.Event;
import com.ironhack.nightoutai.model.Ticket;
import com.ironhack.nightoutai.repository.TicketRepository;
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


    public Ticket addTicket(Ticket ticket) {
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
        if (!ticketRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "El ticket no existe");
        }
        ticketRepository.deleteById(id);
    }

    //Primero verifica si el ticket existe, luego guarda la versión actualizada.
    public Ticket updateTicket(Long id, TicketRequestDto dto) {

        //miramos si el ticket existe
        Ticket existingTicket = findById(id);

       //Buscamos el evento real usando el ID que viene en el DTO
        Event event = eventService.findById(dto.getEventId());


        existingTicket.setPrice(dto.getPrice());
        existingTicket.setEvent(event);

        return ticketRepository.save(existingTicket);
    }
}
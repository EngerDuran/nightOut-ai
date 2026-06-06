package com.ironhack.nightoutai.service;

import com.ironhack.nightoutai.enums.ReservationStatus;
import com.ironhack.nightoutai.model.Booking;
import com.ironhack.nightoutai.model.Event;
import com.ironhack.nightoutai.model.Ticket;
import com.ironhack.nightoutai.repository.BookingRepository;
import com.ironhack.nightoutai.repository.EventRepository;
import com.ironhack.nightoutai.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    //Controlamos una sobreventa de entradas
    @Transactional
    public Booking createBooking(Long ticketId, Integer quantity, String paymentMethod) {

          Ticket ticket = ticketRepository.findById(ticketId)
                  .orElseThrow(() -> new RuntimeException("Ticket not available" + ticketId));

          Event event = ticket.getEvent();

          if (event.getSoldTickets() + quantity > event.getTotalCapacity()) {
              throw new RuntimeException(" ¡SOLD OUT!, Not enough tickets available");
          }

        // Actualizamos el contador de entradas vendidas en el Evento
        event.setSoldTickets(event.getSoldTickets() + quantity);
        eventRepository.save(event);


        // Creamos la instancia de Booking, rellenar los datos y guardarla
        Booking booking = new Booking();
        booking.setTicket(ticket);
        booking.setQuantity(quantity);
        booking.setPaymentMethod(paymentMethod);
        booking.setReservationDate(LocalDateTime.now());
        booking.setStatus(ReservationStatus.CONFIRMED);

      return bookingRepository.save(booking);

    }
}
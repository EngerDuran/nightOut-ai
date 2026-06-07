package com.ironhack.nightoutai.service;

import com.ironhack.nightoutai.enums.ReservationStatus;
import com.ironhack.nightoutai.model.Booking;
import com.ironhack.nightoutai.model.Event;
import com.ironhack.nightoutai.model.Ticket;
import com.ironhack.nightoutai.model.User;
import com.ironhack.nightoutai.repository.BookingRepository;
import com.ironhack.nightoutai.repository.EventRepository;
import com.ironhack.nightoutai.repository.TicketRepository;
import com.ironhack.nightoutai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository; //Inyectado para asociar el usuario del JWT

    // 1. CREAR RESERVA (POST)
    @Transactional
    public Booking createBooking(Long ticketId, Integer quantity, String paymentMethod) {
        // Buscar el ticket con excepción limpia
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found with ID:" + ticketId));

        Event event = ticket.getEvent();

        // Validar aforo
        if (event.getSoldTickets() + quantity > event.getTotalCapacity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "SOLD OUT! Not enough tickets left.");
        }

        // Actualizar contador del evento
        event.setSoldTickets(event.getSoldTickets() + quantity);
        eventRepository.save(event);

        // Obtener el usuario autenticado desde el contexto de Spring Security
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not valid.");
        }

        // Instanciar y guardar la reserva
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTicket(ticket);
        booking.setQuantity(quantity);
        booking.setPaymentMethod(paymentMethod);
        booking.setReservationDate(LocalDateTime.now());
        booking.setStatus(ReservationStatus.CONFIRMED);

        return bookingRepository.save(booking);
    }

    // 2. OBTENER TODAS LAS RESERVAS (GET)
    @Transactional(readOnly = true)
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // 3. OBTENER RESERVA POR ID (GET por ID)
    @Transactional(readOnly = true)
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found with ID: " + id));
    }

    // 4. ACTUALIZAR RESERVA (PUT)
    @Transactional
    public Booking updateBooking(Long id, com.ironhack.nightoutai.dto.BookingRequestDTO request) {
        Booking booking = getBookingById(id);
        Event event = booking.getTicket().getEvent();

        // Si cambia la cantidad de entradas, recalculamos el aforo del evento
        if (request.getQuantity() != null && !request.getQuantity().equals(booking.getQuantity())) {
            int diferencia = request.getQuantity() - booking.getQuantity();

            if (event.getSoldTickets() + diferencia > event.getTotalCapacity()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "It cannot be updated: it exceeds the maximum capacity.");
            }

            event.setSoldTickets(event.getSoldTickets() + diferencia);
            eventRepository.save(event);
            booking.setQuantity(request.getQuantity());
        }

        if (request.getPaymentMethod() != null) {
            booking.setPaymentMethod(request.getPaymentMethod());
        }

        return bookingRepository.save(booking);
    }

    @Transactional
    public void cancelBooking(Long id) {
        Booking booking = getBookingById(id);
        Event event = booking.getTicket().getEvent();

        // Devolvemos el aforo liberado al evento
        event.setSoldTickets(event.getSoldTickets() - booking.getQuantity());
        eventRepository.save(event);

        bookingRepository.delete(booking);
    }
}
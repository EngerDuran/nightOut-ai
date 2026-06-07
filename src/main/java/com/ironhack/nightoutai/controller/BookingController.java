package com.ironhack.nightoutai.controller;

import com.ironhack.nightoutai.dto.BookingRequestDTO;
import com.ironhack.nightoutai.dto.BookingRequestDTO;
import com.ironhack.nightoutai.model.Booking;
import com.ironhack.nightoutai.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings") // ✅ Ruta unificada con el resto de la API
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // 1. CREAR (POST)
    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequestDTO request) {
        Booking newBooking = bookingService.createBooking(
                request.getTicketId(),
                request.getQuantity(),
                request.getPaymentMethod()
        );
        return new ResponseEntity<>(newBooking, HttpStatus.CREATED);
    }

    // 2. LEER TODOS (GET)
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    // 3. LEER POR ID (GET)
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    // 4. ACTUALIZAR (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Booking> updateBooking(@PathVariable Long id, @RequestBody BookingRequestDTO request) {
        return ResponseEntity.ok(bookingService.updateBooking(id, request));
    }

    // 5. BORRAR/CANCELAR (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build(); // Devuelve un 204 No Content
    }
}
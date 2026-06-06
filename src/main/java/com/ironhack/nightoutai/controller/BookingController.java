package com.ironhack.nightoutai.controller;

import com.ironhack.nightoutai.dto.BookingRequestDTO;
import com.ironhack.nightoutai.model.Booking;
import com.ironhack.nightoutai.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequestDTO request) {
      Booking newBooking = bookingService.createBooking(
              request.getTicketId(),
              request.getQuantity(),
              request.getPaymentMethod()
            );

      return new ResponseEntity<>(newBooking, HttpStatus.CREATED);

    }
}

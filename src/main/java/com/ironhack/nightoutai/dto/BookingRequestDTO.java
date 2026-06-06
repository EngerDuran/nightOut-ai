package com.ironhack.nightoutai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class BookingRequestDTO {
    private Long ticketId;
    private Integer quantity;
    private String paymentMethod;
}
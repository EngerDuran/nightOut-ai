package com.ironhack.nightoutai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketRequestDTO {
    // El evento al que pertenece este ticket (se envía como ID)
    private Long eventId;
    private double price;

}
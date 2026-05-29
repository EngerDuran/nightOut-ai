package com.ironhack.nightoutai.shared.web;


import lombok.Data;


import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ErrorResponse {

    private int statusCode;
    private String message;
    private LocalDateTime date;


}

package com.ironhack.nightoutai.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private int statusCode;
    private String message;
    private LocalDateTime date;
}

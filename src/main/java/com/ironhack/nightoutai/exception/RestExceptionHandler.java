package com.ironhack.nightoutai.exception;

import com.ironhack.nightoutai.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestExceptionHandler {
    //Captura las excepciones controladas de Spring (404, 400, 401, etc.)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setStatusCode(ex.getStatusCode().value()); //Capturamos el número de error ej 404.
        error.setMessage(ex.getReason()); //Captura el mensaje personalizado
        error.setDate(LocalDateTime.now());

        return new ResponseEntity<>(error, ex.getStatusCode());
    }
}

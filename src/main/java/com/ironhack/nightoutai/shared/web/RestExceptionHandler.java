package com.ironhack.nightoutai.shared.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RestExceptionHandler{

   public ResponseEntity<ErrorResponse> handleException(Exception ex) {

       //Creamos el objeto Response
       ErrorResponse error = new ErrorResponse();
       error.setStatusCode(HttpStatus.BAD_REQUEST.value());
       error.setMessage(ex.getMessage());
       error.setDate(LocalDateTime.now());

       //Devolvemos la respuesta
       return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
   }

}

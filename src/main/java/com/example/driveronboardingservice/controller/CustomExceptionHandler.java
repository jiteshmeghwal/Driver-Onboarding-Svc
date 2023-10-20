package com.example.driveronboardingservice.controller;

import com.example.driveronboardingservice.exception.GenericException;
import com.example.driveronboardingservice.exception.ResourceNotFoundException;
import com.example.driveronboardingservice.exception.ValidationException;
import com.example.driveronboardingservice.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(GenericException.class)
    public ResponseEntity<Object> handleGenericException(GenericException ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(ex.getCode(), ex.getDesc()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(ex.getCode(), ex.getDesc()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        return new ResponseEntity<>(new ErrorResponse(ex.getCode(), ex.getDesc()), HttpStatus.NOT_FOUND);
    }

}

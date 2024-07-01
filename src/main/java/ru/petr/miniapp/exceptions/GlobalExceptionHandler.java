package ru.petr.miniapp.exceptions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.jdi.InternalException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({JsonProcessingException.class, IOException.class, InternalException.class,
            InvalidKeyException.class, NoSuchAlgorithmException.class, IllegalArgumentException.class,})
    public ResponseEntity<ErrorResponse> handleServerExceptions(Exception exception) {
        ErrorResponse response = new ErrorResponse(
                exception.getMessage(),
                new Date(System.currentTimeMillis())
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<ErrorResponse> handleValidationExceptions(Exception exception) {
        ErrorResponse response = new ErrorResponse(
                exception.getMessage(),
                new Date(System.currentTimeMillis())
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

}


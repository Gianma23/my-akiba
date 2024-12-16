package it.unipi.myakiba.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        // Create a map to hold field-specific error messages
        Map<String, String> errors = new HashMap<>();

        // Loop through each field error
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField(); // Get the field name
            String errorMessage = error.getDefaultMessage();   // Get the error message
            errors.put(fieldName, errorMessage);               // Map field to message
        });

        // Return a bad request response with the error details
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
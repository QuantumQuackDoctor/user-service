package com.ss.user.errors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice
public class ValidationAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(ex.getBindingResult().getAllErrors().stream().map(FieldError.class::cast).collect(
                Collectors.toMap(FieldError::getField,
                        fieldError -> Optional.ofNullable(fieldError.getDefaultMessage()).orElse(""))
        ));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Object> handleInvalidCredentialsException (InvalidCredentialsException e) {
        return ResponseEntity.badRequest().body(e);
    }

    @ExceptionHandler(EmailTakenException.class)
    public ResponseEntity<String> handleEmailTaken(EmailTakenException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}

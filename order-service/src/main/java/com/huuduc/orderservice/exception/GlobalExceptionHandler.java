package com.huuduc.orderservice.exception;

import io.temporal.failure.ApplicationFailure;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

import static com.huuduc.orderservice.exception.ErrorCodes.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@RestControllerAdvice
@SuppressWarnings("unused")
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(EntityNotFoundException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ErrorResponse.builder()
                                .errorCode(ErrorCodes.NOT_FOUND)
                                .error(exception.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {

        var errors = new HashMap<String, String>();
        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    var fieldName = ((FieldError) error).getField();
                    var errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .errorCode(ErrorCodes.INVALID_INPUT)
                                .errors(errors)
                                .build()
                );
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handle(BusinessException exp) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .errorCode(ErrorCodes.BUSINESS_ERROR)
                                .error(exp.getMsg())
                                .build()
                );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {

        return ResponseEntity
                .status(FORBIDDEN)
                .body(
                        ErrorResponse
                                .builder()
                                .errorCode(ACCESS_DENY)
                                .error(ACCESS_DENY.getMsg())
                                .build()
                );
    }

    @ExceptionHandler(ApplicationFailure.class)
    public ResponseEntity<ErrorResponse> handleApplicationFailure(ApplicationFailure ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        ErrorResponse
                                .builder()
                                .errorCode(WORKFLOW_EXCEPTION)
                                .error(WORKFLOW_EXCEPTION.getMsg())
                                .build()
                );
    }
}

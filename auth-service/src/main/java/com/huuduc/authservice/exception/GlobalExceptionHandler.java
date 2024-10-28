package com.huuduc.authservice.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;
import java.util.HashSet;
import java.util.Set;

import static com.huuduc.authservice.exception.BusinessErrorCodes.*;
import static org.springframework.http.HttpStatus.*;

@SuppressWarnings("unused")
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(EntityNotFoundException exception) {

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        ErrorResponse.builder()
                                .ErrorCode(USER_NOT_FOUND.getCode())
                                .ErrorDescription(USER_NOT_FOUND.getDescription())
                                .error(exception.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleException() {

        return ResponseEntity
                .status(UNAUTHORIZED)
                .body(
                        ErrorResponse.builder()
                                .ErrorCode(BAD_CREDENTIALS.getCode())
                                .ErrorDescription(BAD_CREDENTIALS.getDescription())
                                .error(messageSource.getMessage("unauthorized", null, LocaleContextHolder.getLocale()))
                                .build()
                );
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handle(UsernameAlreadyExistsException e) {

        return ResponseEntity
                .status(CONFLICT)
                .body(
                        ErrorResponse.builder()
                                .ErrorCode(CONFLICT_USERNAME.getCode())
                                .ErrorDescription(CONFLICT_USERNAME.getDescription())
                                .error(e.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exp) {

        Set<String> errors = new HashSet<>();

        exp.getBindingResult().getAllErrors()
                .forEach(error -> {
                    //var fieldName = ((FieldError) error).getField();
                    var errorMessage = error.getDefaultMessage();
                    errors.add(errorMessage);
                });

        return ResponseEntity
                .status(BAD_REQUEST)
                .body(
                        ErrorResponse.builder()
                                .validationErrors(errors)
                                .build()
                );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handle(JwtException exp) {

        return ResponseEntity
                .status(FORBIDDEN)
                .body(
                        ErrorResponse.builder()
                                .ErrorCode(INVALID_TOKEN.getCode())
                                .ErrorDescription(INVALID_TOKEN.getDescription())
                                .error(exp.getMessage())
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
                                .ErrorCode(ACCESS_DENY.getCode())
                                .ErrorDescription(ACCESS_DENY.getDescription())
                                .error(ex.getMessage())
                                .build()
                );
    }

//    @ExceptionHandler(Exception.class)
//    public ProblemDetail handleSecurity(Exception ex){
//
//        ProblemDetail errorDetail = null;
//
//        if(ex instanceof AccessDeniedException){
//
//            errorDetail = ProblemDetail
//                    .forStatusAndDetail(HttpStatusCode.valueOf(403),ex.getMessage());
//            errorDetail.setProperty("Access_denied_reason","not_authorized");
//        }
//
//        if(ex instanceof SignatureException){
//
//            errorDetail = ProblemDetail
//                    .forStatusAndDetail(HttpStatusCode.valueOf(403),ex.getMessage());
//            errorDetail.setProperty("Access_denied_reason","JWT Signature not valid");
//        }
//
//        if(ex instanceof ExpiredJwtException){
//
//            errorDetail = ProblemDetail
//                    .forStatusAndDetail(HttpStatusCode.valueOf(403),ex.getMessage());
//            errorDetail.setProperty("Access_denied_reason","JWT expired");
//        }
//
//        return errorDetail;
//    }
}

package com.huuduc.productservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum ErrorCodes {

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "Unauthorized access"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Access is forbidden"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Invalid request data"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
    CONFLICT(HttpStatus.CONFLICT, "Resource conflict"),
    VALIDATION_FAILED(HttpStatus.UNPROCESSABLE_ENTITY, "Validation failed"),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "Too many requests, please try again later"),
    REQUEST_TIMEOUT(HttpStatus.REQUEST_TIMEOUT, "Request timeout"),
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Service is temporarily unavailable"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Invalid credentials"),
    INSUFFICIENT_PERMISSIONS(HttpStatus.FORBIDDEN, "Insufficient permissions"),
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "Invalid input data"),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Database connection error"),
    EXTERNAL_SERVICE_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "External service unavailable"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "Duplicate resource"),
    TOKEN_EXPIRED(HttpStatus.FORBIDDEN, "Token expired"),
    INVALID_TOKEN(HttpStatus.FORBIDDEN, "Invalid token"),
    MISSING_FIELDS(HttpStatus.BAD_REQUEST, "Missing required fields"),
    PAYLOAD_TOO_LARGE(HttpStatus.PAYLOAD_TOO_LARGE, "Request entity too large"),
    DEPENDENCY_FAILURE(HttpStatus.FAILED_DEPENDENCY, "Dependency failure"),
    ACCESS_DENY(HttpStatus.FORBIDDEN,"You do not have permission to access this resource.")
    ;

    @Getter
    private final HttpStatus code;
    @Getter
    private final String msg;

    // Constructor cho enum
    ErrorCodes(HttpStatus code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}

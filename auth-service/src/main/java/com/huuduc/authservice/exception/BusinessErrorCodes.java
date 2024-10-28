package com.huuduc.authservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@SuppressWarnings("unused")
@Getter
public enum BusinessErrorCodes {

    NO_CODE(0, NOT_IMPLEMENTED, "No code"),
    INCORRECT_CURRENT_PASSWORD(300, BAD_REQUEST, "Current password is incorrect"),
    NEW_PASSWORD_DOES_NOT_MATCH(301, BAD_REQUEST, "The new password does not match"),
    BAD_CREDENTIALS(401, UNAUTHORIZED, "Login and / or Password is incorrect"),
    TOKEN_EXPIRED(403, HttpStatus.FORBIDDEN, "Token expired"),
    INVALID_TOKEN(403, HttpStatus.FORBIDDEN, "Invalid token"),
    CONFLICT_USERNAME(409, CONFLICT, "Username already exists"),
    USER_NOT_FOUND(404, NOT_FOUND, "User not found"),
    ACCESS_DENY(403, FORBIDDEN, "You do not have permission to access this resource.");

    private final int code;
    private final String description;
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, HttpStatus status, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = status;
    }
}

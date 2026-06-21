package com.pisethjavaschool.userservice.user.exception;

import org.springframework.http.HttpStatus;

public class InvalidRegistrationTokenException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public InvalidRegistrationTokenException() {
        super(
                "INVALID_REGISTRATION_TOKEN",
                "Registration token is invalid or expired.",
                HttpStatus.UNAUTHORIZED
        );
    }
}
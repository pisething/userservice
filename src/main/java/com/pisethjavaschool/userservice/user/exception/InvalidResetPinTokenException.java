package com.pisethjavaschool.userservice.user.exception;

import org.springframework.http.HttpStatus;

public class InvalidResetPinTokenException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public InvalidResetPinTokenException() {
        super(
                "INVALID_RESET_PIN_TOKEN",
                "Reset PIN token is invalid or expired.",
                HttpStatus.UNAUTHORIZED
        );
    }
}
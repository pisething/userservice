package com.pisethjavaschool.userservice.user.exception;

import org.springframework.http.HttpStatus;

public class InvalidPinException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public InvalidPinException(String message) {
        super("INVALID_PIN", message, HttpStatus.BAD_REQUEST);
    }
}
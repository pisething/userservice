package com.pisethjavaschool.userservice.user.exception;

import org.springframework.http.HttpStatus;

public class InvalidUserTypeException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public InvalidUserTypeException(String message) {
        super("INVALID_USER_TYPE", message, HttpStatus.BAD_REQUEST);
    }
}
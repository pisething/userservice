package com.pisethjavaschool.userservice.user.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public InvalidCredentialException() {
        super(
                "INVALID_CREDENTIALS",
                "Phone number or PIN is incorrect",
                HttpStatus.UNAUTHORIZED
        );
    }
}
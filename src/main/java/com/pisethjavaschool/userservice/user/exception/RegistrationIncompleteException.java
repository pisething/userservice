package com.pisethjavaschool.userservice.user.exception;

import org.springframework.http.HttpStatus;

public class RegistrationIncompleteException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public RegistrationIncompleteException() {
        super(
                "REGISTRATION_INCOMPLETE",
                "Registration is not completed",
                HttpStatus.BAD_REQUEST
        );
    }
}
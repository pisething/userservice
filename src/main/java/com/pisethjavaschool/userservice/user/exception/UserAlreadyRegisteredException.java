package com.pisethjavaschool.userservice.user.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyRegisteredException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public UserAlreadyRegisteredException() {
        super("USER_ALREADY_REGISTERED", "User already registered", HttpStatus.CONFLICT);
    }
}
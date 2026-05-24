package com.pisethjavaschool.userservice.user.exception;

import org.springframework.http.HttpStatus;

public class InvalidOtpException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public InvalidOtpException() {
        super("INVALID_OTP", "Invalid OTP", HttpStatus.BAD_REQUEST);
    }
}
package com.pisethjavaschool.userservice.user.exception;

import org.springframework.http.HttpStatus;

public class OtpMaxAttemptsReachedException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public OtpMaxAttemptsReachedException() {
        super(
                "OTP_MAX_ATTEMPTS_REACHED",
                "OTP max attempts reached",
                HttpStatus.BAD_REQUEST
        );
    }
}
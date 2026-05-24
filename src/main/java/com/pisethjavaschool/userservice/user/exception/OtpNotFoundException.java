package com.pisethjavaschool.userservice.user.exception;

import org.springframework.http.HttpStatus;

public class OtpNotFoundException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public OtpNotFoundException() {
        super(
                "OTP_NOT_FOUND",
                "OTP not found or already used",
                HttpStatus.BAD_REQUEST
        );
    }
}
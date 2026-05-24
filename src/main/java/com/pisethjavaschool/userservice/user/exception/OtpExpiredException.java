package com.pisethjavaschool.userservice.user.exception;

import org.springframework.http.HttpStatus;

public class OtpExpiredException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public OtpExpiredException() {
        super(
                "OTP_EXPIRED",
                "OTP has expired. Please request a new OTP.",
                HttpStatus.BAD_REQUEST
        );
    }
}
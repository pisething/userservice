package com.pisethjavaschool.userservice.user.exception;

import org.springframework.http.HttpStatus;

public class OtpResendNotAllowedException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public OtpResendNotAllowedException() {
        super(
                "OTP_RESEND_NOT_ALLOWED",
                "OTP can only be resent before it has been verified",
                HttpStatus.BAD_REQUEST
        );
    }
}
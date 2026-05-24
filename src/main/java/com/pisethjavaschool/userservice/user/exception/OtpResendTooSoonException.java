package com.pisethjavaschool.userservice.user.exception;

import org.springframework.http.HttpStatus;

public class OtpResendTooSoonException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public OtpResendTooSoonException() {
        super(
                "OTP_RESEND_TOO_SOON",
                "Please wait before requesting another OTP",
                HttpStatus.TOO_MANY_REQUESTS
        );
    }
}
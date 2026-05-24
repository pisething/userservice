package com.pisethjavaschool.userservice.user.exception;

import org.springframework.http.HttpStatus;

public class OtpNotVerifiedException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public OtpNotVerifiedException() {
        super(
                "OTP_NOT_VERIFIED",
                "OTP must be verified before completing profile",
                HttpStatus.BAD_REQUEST
        );
    }
}
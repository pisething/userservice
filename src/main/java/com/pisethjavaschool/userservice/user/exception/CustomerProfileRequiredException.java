package com.pisethjavaschool.userservice.user.exception;

import org.springframework.http.HttpStatus;

public class CustomerProfileRequiredException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public CustomerProfileRequiredException() {
        super(
                "CUSTOMER_PROFILE_REQUIRED",
                "Customer profile is required before setting PIN",
                HttpStatus.BAD_REQUEST
        );
    }
}
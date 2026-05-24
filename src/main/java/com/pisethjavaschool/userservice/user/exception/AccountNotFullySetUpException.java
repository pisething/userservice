package com.pisethjavaschool.userservice.user.exception;

import org.springframework.http.HttpStatus;

public class AccountNotFullySetUpException extends BusinessException {

    public AccountNotFullySetUpException() {
        super(
                "ACCOUNT_NOT_FULLY_SET_UP",
                "Your account setup is incomplete. Please contact support.",
                HttpStatus.CONFLICT
        );
    }
}
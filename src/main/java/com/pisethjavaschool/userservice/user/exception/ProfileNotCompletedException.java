package com.pisethjavaschool.userservice.user.exception;

import org.springframework.http.HttpStatus;

public class ProfileNotCompletedException extends BusinessException {

    private static final long serialVersionUID = 1L;

    public ProfileNotCompletedException() {
        super(
                "PROFILE_NOT_COMPLETED",
                "Profile must be completed before setting PIN",
                HttpStatus.BAD_REQUEST
        );
    }
}
package com.pisethjavaschool.userservice.user.validation;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.exception.InvalidPinException;

@Component
public class PinValidator {

    public void validateMatched(String pin, String confirmPin) {
        if (!pin.equals(confirmPin)) {
            throw new InvalidPinException("PIN and confirm PIN do not match");
        }
    }
}
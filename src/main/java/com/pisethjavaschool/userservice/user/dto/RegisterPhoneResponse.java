package com.pisethjavaschool.userservice.user.dto;

import java.util.UUID;

import com.pisethjavaschool.userservice.user.domain.enumeration.RegistrationStatus;

public record RegisterPhoneResponse(
        UUID userAccountId,
        RegistrationStatus registrationStatus,
        String message
) {
}

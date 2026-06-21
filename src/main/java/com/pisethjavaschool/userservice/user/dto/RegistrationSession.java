package com.pisethjavaschool.userservice.user.dto;

import java.time.Instant;
import java.util.UUID;

import com.pisethjavaschool.userservice.user.domain.enumeration.RegistrationStatus;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;

public record RegistrationSession(
        UUID userAccountId,
        UserType userType,
        RegistrationStatus registrationStatus,
        Instant createdAt,
        Instant expiresAt
) {
}
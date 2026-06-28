package com.pisethjavaschool.userservice.user.dto;

import java.time.Instant;
import java.util.UUID;

import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;

public record ResetPinSession(
        UUID userAccountId,
        UserType userType,
        Instant createdAt,
        Instant expiresAt
) {
}
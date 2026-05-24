package com.pisethjavaschool.userservice.user.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.pisethjavaschool.userservice.user.domain.enumeration.Gender;

public record CustomerProfileResponse(
        UUID id,
        UUID userAccountId,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Gender gender,
        String email,
        String nid,
        String referralCode,
        String photoObjectKey,
        String photoUrl
) {
}

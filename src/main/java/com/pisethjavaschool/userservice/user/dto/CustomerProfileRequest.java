package com.pisethjavaschool.userservice.user.dto;

import java.time.LocalDate;

import com.pisethjavaschool.userservice.user.domain.enumeration.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomerProfileRequest(
		@NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull LocalDate dateOfBirth,
        @NotNull Gender gender,
        @Email String email,
        String nid,
        String referralCode,
        String photoObjectKey
) {
}

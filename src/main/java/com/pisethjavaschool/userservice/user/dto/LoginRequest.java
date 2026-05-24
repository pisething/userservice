package com.pisethjavaschool.userservice.user.dto;

import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotBlank String countryCode,
        @NotBlank String phoneNumber,
        @NotNull UserType userType,
        @NotBlank String pin
) {
}

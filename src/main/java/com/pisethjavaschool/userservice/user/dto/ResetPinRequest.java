package com.pisethjavaschool.userservice.user.dto;

import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ResetPinRequest(
        @NotBlank String countryCode,
        @NotBlank String phoneNumber,
        @NotNull UserType userType,
        @NotBlank @Pattern(regexp = "^[0-9]{6}$") String otpCode,
        @NotBlank @Pattern(regexp = "^[0-9]{4,6}$") String pin,
        @NotBlank @Pattern(regexp = "^[0-9]{4,6}$") String confirmPin
) {
}

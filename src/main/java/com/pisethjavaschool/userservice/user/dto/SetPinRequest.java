package com.pisethjavaschool.userservice.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SetPinRequest(
        @NotBlank @Pattern(regexp = "^[0-9]{4,6}$") String pin,
        @NotBlank @Pattern(regexp = "^[0-9]{4,6}$") String confirmPin
) {
}

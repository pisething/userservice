package com.pisethjavaschool.userservice.user.dto;

public record NormalizedPhone(
        String countryCode,
        String phoneNumber
) {
}
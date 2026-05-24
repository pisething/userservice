package com.pisethjavaschool.userservice.user.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String tokenType
) {
}

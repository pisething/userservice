package com.pisethjavaschool.userservice.user.service.keycloak.dto;

public record KeycloakResetPasswordRequest(
        String keycloakUserId,
        String password,
        boolean temporary
) {
}
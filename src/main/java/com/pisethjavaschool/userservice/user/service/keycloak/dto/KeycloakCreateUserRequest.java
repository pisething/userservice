package com.pisethjavaschool.userservice.user.service.keycloak.dto;

import java.util.Map;

public record KeycloakCreateUserRequest(
        String username,
        String firstName,
        String lastName,
        String email,
        String password,
        Map<String, String> attributes
) {
}
package com.pisethjavaschool.userservice.user.service.keycloak;

import com.pisethjavaschool.userservice.user.service.keycloak.dto.KeycloakCreateUserRequest;
import com.pisethjavaschool.userservice.user.service.keycloak.dto.KeycloakResetPasswordRequest;

import reactor.core.publisher.Mono;

public interface KeycloakAdminClient {

    Mono<String> createUser(KeycloakCreateUserRequest request);

    Mono<Void> resetPassword(KeycloakResetPasswordRequest request);
}
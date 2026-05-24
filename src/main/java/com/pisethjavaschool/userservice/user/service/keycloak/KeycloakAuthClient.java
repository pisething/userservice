package com.pisethjavaschool.userservice.user.service.keycloak;

import com.pisethjavaschool.userservice.user.dto.LoginResponse;

import reactor.core.publisher.Mono;

public interface KeycloakAuthClient {

    Mono<LoginResponse> login(String username, String pin);
}

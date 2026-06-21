package com.pisethjavaschool.userservice.user.facade.auth;

import com.pisethjavaschool.userservice.user.dto.LoginRequest;
import com.pisethjavaschool.userservice.user.dto.LoginResponse;

import reactor.core.publisher.Mono;

public interface LoginFacade {
    Mono<LoginResponse> execute(LoginRequest request);
}


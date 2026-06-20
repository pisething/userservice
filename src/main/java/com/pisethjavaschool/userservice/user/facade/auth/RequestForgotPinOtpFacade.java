package com.pisethjavaschool.userservice.user.facade.auth;

import com.pisethjavaschool.userservice.user.dto.ForgotPinRequest;

import reactor.core.publisher.Mono;

public interface RequestForgotPinOtpFacade {
    Mono<Void> execute(ForgotPinRequest request);
}
package com.pisethjavaschool.userservice.user.facade.auth;

import com.pisethjavaschool.userservice.user.dto.ResetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;

import reactor.core.publisher.Mono;

public interface ResetPinFacade {
    Mono<UserAccountResponse> execute(String resetToken, ResetPinRequest request);
}
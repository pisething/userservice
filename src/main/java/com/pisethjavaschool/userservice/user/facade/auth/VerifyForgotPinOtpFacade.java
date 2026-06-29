package com.pisethjavaschool.userservice.user.facade.auth;

import com.pisethjavaschool.userservice.user.dto.VerifyForgotPinOtpRequest;
import com.pisethjavaschool.userservice.user.dto.VerifyForgotPinOtpResponse;

import reactor.core.publisher.Mono;

public interface VerifyForgotPinOtpFacade {

    Mono<VerifyForgotPinOtpResponse> execute(VerifyForgotPinOtpRequest request);
}
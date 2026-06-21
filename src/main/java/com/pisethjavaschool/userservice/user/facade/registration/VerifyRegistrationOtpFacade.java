package com.pisethjavaschool.userservice.user.facade.registration;

import com.pisethjavaschool.userservice.user.dto.RegistrationStatusResponse;
import com.pisethjavaschool.userservice.user.dto.VerifyOtpRequest;

import reactor.core.publisher.Mono;

public interface VerifyRegistrationOtpFacade {

    Mono<RegistrationStatusResponse> execute(VerifyOtpRequest request);
}
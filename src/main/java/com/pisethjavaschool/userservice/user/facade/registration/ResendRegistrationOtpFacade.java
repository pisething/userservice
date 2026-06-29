package com.pisethjavaschool.userservice.user.facade.registration;

import com.pisethjavaschool.userservice.user.dto.RegisterPhoneRequest;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneResponse;

import reactor.core.publisher.Mono;

public interface ResendRegistrationOtpFacade {

    Mono<RegisterPhoneResponse> execute(RegisterPhoneRequest request);
}
package com.pisethjavaschool.userservice.user.facade.registration;

import com.pisethjavaschool.userservice.user.dto.RegisterPhoneRequest;
import com.pisethjavaschool.userservice.user.dto.RegistrationStatusResponse;

import reactor.core.publisher.Mono;

public interface ResumeRegistrationFacade {

    Mono<RegistrationStatusResponse> execute(RegisterPhoneRequest request);
}
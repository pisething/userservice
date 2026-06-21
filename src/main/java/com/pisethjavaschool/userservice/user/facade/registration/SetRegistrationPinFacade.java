package com.pisethjavaschool.userservice.user.facade.registration;

import java.util.UUID;

import com.pisethjavaschool.userservice.user.dto.SetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;

import reactor.core.publisher.Mono;

public interface SetRegistrationPinFacade {

    Mono<UserAccountResponse> execute(UUID userAccountId, SetPinRequest request);
}
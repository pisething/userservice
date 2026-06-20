package com.pisethjavaschool.userservice.user.facade.registration;

import com.pisethjavaschool.userservice.user.dto.SetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SetRegistrationPinFacade {

    Mono<UserAccountResponse> execute(UUID userAccountId, SetPinRequest request);
}
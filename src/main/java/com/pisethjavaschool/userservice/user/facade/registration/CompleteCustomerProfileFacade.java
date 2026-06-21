package com.pisethjavaschool.userservice.user.facade.registration;

import java.util.UUID;

import com.pisethjavaschool.userservice.user.dto.CustomerProfileRequest;
import com.pisethjavaschool.userservice.user.dto.CustomerProfileResponse;

import reactor.core.publisher.Mono;

public interface CompleteCustomerProfileFacade {

    Mono<CustomerProfileResponse> execute(UUID userAccountId, String registrationToken, CustomerProfileRequest request);
}
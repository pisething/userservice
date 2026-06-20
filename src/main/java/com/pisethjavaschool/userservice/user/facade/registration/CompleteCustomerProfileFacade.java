package com.pisethjavaschool.userservice.user.facade.registration;

import com.pisethjavaschool.userservice.user.dto.CustomerProfileRequest;
import com.pisethjavaschool.userservice.user.dto.CustomerProfileResponse;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CompleteCustomerProfileFacade {

    Mono<CustomerProfileResponse> execute(UUID userAccountId, CustomerProfileRequest request);
}
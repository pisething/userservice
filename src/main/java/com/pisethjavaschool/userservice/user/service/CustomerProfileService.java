package com.pisethjavaschool.userservice.user.service;

import java.util.UUID;

import com.pisethjavaschool.userservice.user.domain.CustomerProfile;
import com.pisethjavaschool.userservice.user.dto.CustomerProfileRequest;

import reactor.core.publisher.Mono;

public interface CustomerProfileService {

    Mono<CustomerProfile> upsert(UUID userAccountId, CustomerProfileRequest request);

    Mono<CustomerProfile> findRequiredByUserAccountId(UUID userAccountId);
}
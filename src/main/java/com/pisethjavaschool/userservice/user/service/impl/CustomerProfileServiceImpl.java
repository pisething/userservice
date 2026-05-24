package com.pisethjavaschool.userservice.user.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pisethjavaschool.userservice.user.domain.CustomerProfile;
import com.pisethjavaschool.userservice.user.dto.CustomerProfileRequest;
import com.pisethjavaschool.userservice.user.exception.CustomerProfileRequiredException;
import com.pisethjavaschool.userservice.user.factory.ProfileFactory;
import com.pisethjavaschool.userservice.user.repository.CustomerProfileRepository;
import com.pisethjavaschool.userservice.user.service.CustomerProfileService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private final CustomerProfileRepository repository;
    private final ProfileFactory profileFactory;

    @Override
    public Mono<CustomerProfile> upsert(UUID userAccountId, CustomerProfileRequest request) {
        return repository.findByUserAccountId(userAccountId)
                .flatMap(existingProfile -> update(existingProfile, request))
                .switchIfEmpty(create(userAccountId, request));
    }

    @Override
    public Mono<CustomerProfile> findRequiredByUserAccountId(UUID userAccountId) {
        return repository.findByUserAccountId(userAccountId)
                .switchIfEmpty(Mono.error(new CustomerProfileRequiredException()));
    }

    private Mono<CustomerProfile> create(UUID userAccountId, CustomerProfileRequest request) {
        CustomerProfile profile = profileFactory.createCustomerProfile(userAccountId, request);

        return repository.save(profile);
    }

    private Mono<CustomerProfile> update(CustomerProfile profile, CustomerProfileRequest request) {
        profileFactory.updateCustomerProfile(profile, request);

        return repository.save(profile);
    }
}
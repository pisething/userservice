package com.pisethjavaschool.userservice.user.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.pisethjavaschool.userservice.user.domain.CustomerProfile;

import reactor.core.publisher.Mono;

public interface CustomerProfileRepository extends ReactiveCrudRepository<CustomerProfile, UUID> {

    Mono<CustomerProfile> findByUserAccountId(UUID userAccountId);
}

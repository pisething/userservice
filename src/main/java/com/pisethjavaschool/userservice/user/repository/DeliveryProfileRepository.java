package com.pisethjavaschool.userservice.user.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.pisethjavaschool.userservice.user.domain.DeliveryProfile;

import reactor.core.publisher.Mono;

public interface DeliveryProfileRepository extends ReactiveCrudRepository<DeliveryProfile, UUID> {

    Mono<DeliveryProfile> findByUserAccountId(UUID userAccountId);
}

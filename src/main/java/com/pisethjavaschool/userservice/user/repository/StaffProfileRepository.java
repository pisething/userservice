package com.pisethjavaschool.userservice.user.repository;

import java.util.UUID;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.pisethjavaschool.userservice.user.domain.StaffProfile;

import reactor.core.publisher.Mono;

public interface StaffProfileRepository extends ReactiveCrudRepository<StaffProfile, UUID> {

    Mono<StaffProfile> findByUserAccountId(UUID userAccountId);
}

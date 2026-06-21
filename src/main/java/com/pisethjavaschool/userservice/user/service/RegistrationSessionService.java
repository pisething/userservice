package com.pisethjavaschool.userservice.user.service;

import java.util.UUID;

import com.pisethjavaschool.userservice.user.domain.enumeration.RegistrationStatus;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;
import com.pisethjavaschool.userservice.user.dto.RegistrationSession;

import reactor.core.publisher.Mono;

public interface RegistrationSessionService {

    Mono<String> createSession(
            UUID userAccountId,
            UserType userType,
            RegistrationStatus registrationStatus
    );

    Mono<RegistrationSession> getRequiredSession(String token);

    Mono<RegistrationSession> getRequiredSessionForUser(
            String token,
            UUID userAccountId
    );

    Mono<Void> updateStatus(
            String token,
            RegistrationStatus registrationStatus
    );

    Mono<Void> deleteSession(String token);
}
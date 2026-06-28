package com.pisethjavaschool.userservice.user.service;

import java.util.UUID;

import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;
import com.pisethjavaschool.userservice.user.dto.ResetPinSession;

import reactor.core.publisher.Mono;

public interface ResetPinSessionService {

    Mono<String> createSession(UUID userAccountId, UserType userType);

    Mono<ResetPinSession> getRequiredSession(String token);

    Mono<Void> deleteSession(String token);
}
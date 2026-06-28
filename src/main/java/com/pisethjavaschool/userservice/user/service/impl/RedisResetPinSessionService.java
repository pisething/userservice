package com.pisethjavaschool.userservice.user.service.impl;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pisethjavaschool.userservice.config.ResetPinSessionProperties;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;
import com.pisethjavaschool.userservice.user.dto.ResetPinSession;
import com.pisethjavaschool.userservice.user.exception.InvalidResetPinTokenException;
import com.pisethjavaschool.userservice.user.service.ResetPinSessionService;
import com.pisethjavaschool.userservice.user.session.RedisSessionStore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisResetPinSessionService implements ResetPinSessionService {

    private static final String KEY_PREFIX = "reset-pin-session:";

    private final RedisSessionStore sessionStore;
    private final ResetPinSessionProperties properties;
    private final Clock clock;

    @Override
    public Mono<String> createSession(UUID userAccountId, UserType userType) {
        String token = UUID.randomUUID().toString();
        String key = buildKey(token);

        Instant now = Instant.now(clock);
        Instant expiresAt = now.plus(Duration.ofMinutes(properties.expireMinutes()));

        ResetPinSession session = new ResetPinSession(
                userAccountId,
                userType,
                now,
                expiresAt
        );

        return sessionStore.save(
                        key,
                        session,
                        Duration.ofMinutes(properties.expireMinutes()),
                        InvalidResetPinTokenException::new
                )
                .thenReturn(token)
                .doOnSuccess(createdToken -> log.info(
                        "Reset PIN session created. userAccountId={}, userType={}, expiresAt={}",
                        userAccountId,
                        userType,
                        expiresAt
                ))
                .doOnError(error -> log.error(
                        "Failed to create reset PIN session. userAccountId={}, reason={}",
                        userAccountId,
                        error.getMessage()
                ));
    }

    @Override
    public Mono<ResetPinSession> getRequiredSession(String token) {
        if (token == null || token.isBlank()) {
            return Mono.error(new InvalidResetPinTokenException());
        }

        return sessionStore.getRequired(
                buildKey(token),
                ResetPinSession.class,
                ResetPinSession::expiresAt,
                InvalidResetPinTokenException::new
        );
    }

    @Override
    public Mono<Void> deleteSession(String token) {
        if (token == null || token.isBlank()) {
            return Mono.empty();
        }

        return sessionStore.delete(buildKey(token))
                .doOnSuccess(unused -> log.info("Reset PIN session deleted."));
    }

    private String buildKey(String token) {
        return KEY_PREFIX + token;
    }
}
package com.pisethjavaschool.userservice.user.service.impl;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pisethjavaschool.userservice.config.RegistrationSessionProperties;
import com.pisethjavaschool.userservice.user.domain.enumeration.RegistrationStatus;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;
import com.pisethjavaschool.userservice.user.dto.RegistrationSession;
import com.pisethjavaschool.userservice.user.exception.InvalidRegistrationTokenException;
import com.pisethjavaschool.userservice.user.service.RegistrationSessionService;
import com.pisethjavaschool.userservice.user.session.RedisSessionStore;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisRegistrationSessionService implements RegistrationSessionService {

    private static final String KEY_PREFIX = "registration-session:";

    private final RedisSessionStore sessionStore;
    private final RegistrationSessionProperties properties;
    private final Clock clock;

    @Override
    public Mono<String> createSession(
            UUID userAccountId,
            UserType userType,
            RegistrationStatus registrationStatus
    ) {
        String token = UUID.randomUUID().toString();
        String key = buildKey(token);

        Instant now = Instant.now(clock);
        Instant expiresAt = now.plus(Duration.ofMinutes(properties.expireMinutes()));

        RegistrationSession session = new RegistrationSession(
                userAccountId,
                userType,
                registrationStatus,
                now,
                expiresAt
        );

        return sessionStore.save(
                        key,
                        session,
                        Duration.ofMinutes(properties.expireMinutes()),
                        InvalidRegistrationTokenException::new
                )
                .thenReturn(token)
                .doOnSuccess(createdToken -> log.info(
                        "Registration session created. userAccountId={}, userType={}, status={}, expiresAt={}",
                        userAccountId,
                        userType,
                        registrationStatus,
                        expiresAt
                ))
                .doOnError(error -> log.error(
                        "Failed to create registration session. userAccountId={}, reason={}",
                        userAccountId,
                        error.getMessage()
                ));
    }

    @Override
    public Mono<RegistrationSession> getRequiredSession(String token) {
        if (token == null || token.isBlank()) {
            return Mono.error(new InvalidRegistrationTokenException());
        }

        return sessionStore.getRequired(
                buildKey(token),
                RegistrationSession.class,
                RegistrationSession::expiresAt,
                InvalidRegistrationTokenException::new
        );
    }

    @Override
    public Mono<RegistrationSession> getRequiredSessionForUser(
            String token,
            UUID userAccountId
    ) {
        return getRequiredSession(token)
                .flatMap(session -> {
                    if (!session.userAccountId().equals(userAccountId)) {
                        return Mono.error(new InvalidRegistrationTokenException());
                    }

                    return Mono.just(session);
                });
    }

    @Override
    public Mono<Void> updateStatus(
            String token,
            RegistrationStatus registrationStatus
    ) {
        return getRequiredSession(token)
                .flatMap(existingSession -> {
                    RegistrationSession updatedSession = new RegistrationSession(
                            existingSession.userAccountId(),
                            existingSession.userType(),
                            registrationStatus,
                            existingSession.createdAt(),
                            existingSession.expiresAt()
                    );

                    Duration remainingTtl = Duration.between(
                            Instant.now(clock),
                            existingSession.expiresAt()
                    );

                    if (remainingTtl.isZero() || remainingTtl.isNegative()) {
                        return Mono.error(new InvalidRegistrationTokenException());
                    }

                    return sessionStore.save(
                            buildKey(token),
                            updatedSession,
                            remainingTtl,
                            InvalidRegistrationTokenException::new
                    );
                })
                .doOnSuccess(unused -> log.info(
                        "Registration session status updated. status={}",
                        registrationStatus
                ));
    }

    @Override
    public Mono<Void> deleteSession(String token) {
        if (token == null || token.isBlank()) {
            return Mono.empty();
        }

        return sessionStore.delete(buildKey(token))
                .doOnSuccess(unused -> log.info("Registration session deleted."));
    }

    private String buildKey(String token) {
        return KEY_PREFIX + token;
    }
}
package com.pisethjavaschool.userservice.user.session;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisSessionStore {

    private final ReactiveStringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    public <T> Mono<Void> save(
            String key,
            T session,
            Duration ttl,
            Supplier<? extends RuntimeException> invalidTokenExceptionSupplier
    ) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(session))
                .onErrorMap(JsonProcessingException.class, exception -> {
                    log.warn("Failed to serialize Redis session. key={}, reason={}", key, exception.getMessage());
                    return invalidTokenExceptionSupplier.get();
                })
                .flatMap(json -> redisTemplate.opsForValue().set(key, json, ttl))
                .then();
    }

    public <T> Mono<T> getRequired(
            String key,
            Class<T> sessionType,
            SessionExpirationReader<T> expirationReader,
            Supplier<? extends RuntimeException> invalidTokenExceptionSupplier
    ) {
        return redisTemplate.opsForValue()
                .get(key)
                .switchIfEmpty(Mono.error(invalidTokenExceptionSupplier.get()))
                .flatMap(json -> deserialize(key, json, sessionType, invalidTokenExceptionSupplier))
                .flatMap(session -> validateNotExpired(session, expirationReader, invalidTokenExceptionSupplier));
    }

    public Mono<Void> delete(String key) {
        return redisTemplate.delete(key).then();
    }

    private <T> Mono<T> deserialize(
            String key,
            String json,
            Class<T> sessionType,
            Supplier<? extends RuntimeException> invalidTokenExceptionSupplier
    ) {
        try {
            return Mono.just(objectMapper.readValue(json, sessionType));
        } catch (JsonProcessingException exception) {
            log.warn("Failed to deserialize Redis session. key={}, reason={}", key, exception.getMessage());
            return Mono.error(invalidTokenExceptionSupplier.get());
        }
    }

    private <T> Mono<T> validateNotExpired(
            T session,
            SessionExpirationReader<T> expirationReader,
            Supplier<? extends RuntimeException> invalidTokenExceptionSupplier
    ) {
        Instant expiresAt = expirationReader.expiresAt(session);

        if (expiresAt == null || !expiresAt.isAfter(Instant.now(clock))) {
            return Mono.error(invalidTokenExceptionSupplier.get());
        }

        return Mono.just(session);
    }
}
package com.pisethjavaschool.userservice.user.session;

import java.time.Instant;

@FunctionalInterface
public interface SessionExpirationReader<T> {

    Instant expiresAt(T session);
}

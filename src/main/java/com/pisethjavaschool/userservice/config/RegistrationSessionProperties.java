package com.pisethjavaschool.userservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "registration-session")
public record RegistrationSessionProperties(
        long expireMinutes
) {
}
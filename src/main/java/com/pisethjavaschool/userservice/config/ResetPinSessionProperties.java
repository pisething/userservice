package com.pisethjavaschool.userservice.config;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "reset-pin-session")
public record ResetPinSessionProperties(
        long expireMinutes
) {
}
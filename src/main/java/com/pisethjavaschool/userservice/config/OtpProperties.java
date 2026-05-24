package com.pisethjavaschool.userservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "otp")
public record OtpProperties(
        Integer expireMinutes,
        Integer resendAfterSeconds,
        Integer maxAttempts
) {
}

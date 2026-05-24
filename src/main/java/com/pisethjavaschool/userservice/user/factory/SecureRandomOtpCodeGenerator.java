package com.pisethjavaschool.userservice.user.factory;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class SecureRandomOtpCodeGenerator implements OtpCodeGenerator {

    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate() {
        return String.format("%06d", random.nextInt(1_000_000));
    }
}
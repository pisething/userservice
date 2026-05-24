package com.pisethjavaschool.userservice.user.service.impl;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pisethjavaschool.userservice.user.service.OtpHashingService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BCryptOtpHashingService implements OtpHashingService {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String hash(String rawOtp) {
        return passwordEncoder.encode(rawOtp);
    }

    @Override
    public boolean matches(String rawOtp, String otpHash) {
        return passwordEncoder.matches(rawOtp, otpHash);
    }
}
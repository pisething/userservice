package com.pisethjavaschool.userservice.user.service;

import com.pisethjavaschool.userservice.user.domain.enumeration.OtpPurpose;

import reactor.core.publisher.Mono;

public interface OtpService {

    Mono<Void> sendOtp(String countryCode, String phoneNumber, OtpPurpose purpose);

    Mono<Void> verifyOtp(String countryCode, String phoneNumber, OtpPurpose purpose, String otpCode);
}

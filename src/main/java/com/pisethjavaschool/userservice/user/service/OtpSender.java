package com.pisethjavaschool.userservice.user.service;

import com.pisethjavaschool.userservice.user.domain.PhoneOtp;

import reactor.core.publisher.Mono;

public interface OtpSender {

    Mono<Void> send(PhoneOtp otp);
}
package com.pisethjavaschool.userservice.user.service;

import com.pisethjavaschool.userservice.user.dto.ForgotPinRequest;
import com.pisethjavaschool.userservice.user.dto.LoginRequest;
import com.pisethjavaschool.userservice.user.dto.LoginResponse;
import com.pisethjavaschool.userservice.user.dto.ResetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;

import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<LoginResponse> login(LoginRequest request);

    Mono<Void> requestForgotPinOtp(ForgotPinRequest request);

    Mono<UserAccountResponse> resetPin(ResetPinRequest request);
}

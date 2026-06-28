package com.pisethjavaschool.userservice.user.service;

import com.pisethjavaschool.userservice.user.dto.ForgotPinRequest;
import com.pisethjavaschool.userservice.user.dto.LoginRequest;
import com.pisethjavaschool.userservice.user.dto.LoginResponse;
import com.pisethjavaschool.userservice.user.dto.ResetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;
import com.pisethjavaschool.userservice.user.dto.VerifyForgotPinOtpRequest;
import com.pisethjavaschool.userservice.user.dto.VerifyForgotPinOtpResponse;

import reactor.core.publisher.Mono;

public interface AuthService {

    Mono<LoginResponse> login(LoginRequest request);

    Mono<Void> requestForgotPinOtp(ForgotPinRequest request);

    Mono<UserAccountResponse> resetPin(String resetToken, ResetPinRequest request);
    
    Mono<VerifyForgotPinOtpResponse> verifyForgotPinOtp(VerifyForgotPinOtpRequest request);
}

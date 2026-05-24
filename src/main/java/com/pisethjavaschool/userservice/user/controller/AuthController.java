package com.pisethjavaschool.userservice.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pisethjavaschool.userservice.user.dto.ForgotPinRequest;
import com.pisethjavaschool.userservice.user.dto.LoginRequest;
import com.pisethjavaschool.userservice.user.dto.LoginResponse;
import com.pisethjavaschool.userservice.user.dto.ResetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;
import com.pisethjavaschool.userservice.user.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public Mono<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }

    @PostMapping("/forgot-pin/otp")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> requestForgotPinOtp(@Valid @RequestBody ForgotPinRequest request) {
        return service.requestForgotPinOtp(request);
    }

    @PostMapping("/forgot-pin/reset")
    public Mono<UserAccountResponse> resetPin(@Valid @RequestBody ResetPinRequest request) {
        return service.resetPin(request);
    }
}

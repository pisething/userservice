package com.pisethjavaschool.userservice.user.service.impl;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pisethjavaschool.userservice.user.dto.ForgotPinRequest;
import com.pisethjavaschool.userservice.user.dto.LoginRequest;
import com.pisethjavaschool.userservice.user.dto.LoginResponse;
import com.pisethjavaschool.userservice.user.dto.ResetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;
import com.pisethjavaschool.userservice.user.dto.VerifyForgotPinOtpRequest;
import com.pisethjavaschool.userservice.user.dto.VerifyForgotPinOtpResponse;
import com.pisethjavaschool.userservice.user.facade.auth.LoginFacade;
import com.pisethjavaschool.userservice.user.facade.auth.RequestForgotPinOtpFacade;
import com.pisethjavaschool.userservice.user.facade.auth.ResetPinFacade;
import com.pisethjavaschool.userservice.user.facade.auth.VerifyForgotPinOtpFacade;
import com.pisethjavaschool.userservice.user.service.AuthService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final LoginFacade loginFacade;
    private final RequestForgotPinOtpFacade requestForgotPinOtpFacade;
    private final ResetPinFacade resetPinFacade;
    private final VerifyForgotPinOtpFacade verifyForgotPinOtpFacade;

    
    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        return loginFacade.execute(request);
    }

    @Override
    public Mono<Void> requestForgotPinOtp(ForgotPinRequest request) {
        return requestForgotPinOtpFacade.execute(request);
    }

    @Override
    @Transactional
    public Mono<UserAccountResponse> resetPin(String resetToken, ResetPinRequest request) {
        return resetPinFacade.execute(resetToken, request);
    }
    
    @Override
    @Transactional
    public Mono<VerifyForgotPinOtpResponse> verifyForgotPinOtp(VerifyForgotPinOtpRequest request) {
        return verifyForgotPinOtpFacade.execute(request);
    }
}
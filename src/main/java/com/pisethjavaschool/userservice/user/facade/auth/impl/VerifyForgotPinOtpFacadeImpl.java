package com.pisethjavaschool.userservice.user.facade.auth.impl;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.enumeration.OtpPurpose;
import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.dto.VerifyForgotPinOtpRequest;
import com.pisethjavaschool.userservice.user.dto.VerifyForgotPinOtpResponse;
import com.pisethjavaschool.userservice.user.facade.auth.VerifyForgotPinOtpFacade;
import com.pisethjavaschool.userservice.user.service.OtpService;
import com.pisethjavaschool.userservice.user.service.PhoneNumberService;
import com.pisethjavaschool.userservice.user.service.ResetPinSessionService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.util.LogMasker;
import com.pisethjavaschool.userservice.user.validation.LoginValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerifyForgotPinOtpFacadeImpl implements VerifyForgotPinOtpFacade {

    private final PhoneNumberService phoneNumberService;
    private final UserAccountFinder userAccountFinder;
    private final LoginValidator loginValidator;
    private final OtpService otpService;
    private final ResetPinSessionService resetPinSessionService;

    @Override
    public Mono<VerifyForgotPinOtpResponse> execute(VerifyForgotPinOtpRequest request) {
        NormalizedPhone phone = phoneNumberService.normalize(
                request.countryCode(),
                request.phoneNumber()
        );

        log.info(
                "Forgot PIN OTP verification requested. userType={}, phone={}",
                request.userType(),
                LogMasker.maskPhone(phone.phoneNumber())
        );

        return userAccountFinder.findRequiredByPhoneAndUserType(phone, request.userType())
                .flatMap(account -> loginValidator.validateCanLogin(account)
                        .then(otpService.verifyOtp(
                                phone.countryCode(),
                                phone.phoneNumber(),
                                OtpPurpose.FORGOT_PIN,
                                request.otpCode()
                        ))
                        .then(resetPinSessionService.createSession(
                                account.getId(),
                                account.getUserType()
                        )))
                .map(VerifyForgotPinOtpResponse::new)
                .doOnSuccess(response -> log.info(
                        "Forgot PIN OTP verified. userType={}, phone={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber())
                ))
                .doOnError(error -> log.warn(
                        "Forgot PIN OTP verification failed. userType={}, phone={}, reason={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        error.getMessage()
                ));
    }
}
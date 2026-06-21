package com.pisethjavaschool.userservice.user.facade.registration.impl;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.enumeration.OtpPurpose;
import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.dto.RegistrationStatusResponse;
import com.pisethjavaschool.userservice.user.dto.VerifyOtpRequest;
import com.pisethjavaschool.userservice.user.facade.registration.VerifyRegistrationOtpFacade;
import com.pisethjavaschool.userservice.user.mapper.RegistrationStatusMapper;
import com.pisethjavaschool.userservice.user.service.OtpService;
import com.pisethjavaschool.userservice.user.service.PhoneNumberService;
import com.pisethjavaschool.userservice.user.service.RegistrationSessionService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.service.UserAccountStateService;
import com.pisethjavaschool.userservice.user.util.LogMasker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerifyRegistrationOtpFacadeImpl implements VerifyRegistrationOtpFacade {
	private final PhoneNumberService phoneNumberService;
    private final UserAccountFinder userAccountFinder;
    private final OtpService otpService;
    private final UserAccountStateService userAccountStateService;
    private final RegistrationStatusMapper registrationStatusMapper;
    private final RegistrationSessionService registrationSessionService;

    @Override
    public Mono<RegistrationStatusResponse> execute(VerifyOtpRequest request) {
        NormalizedPhone phone = phoneNumberService.normalize(
                request.countryCode(),
                request.phoneNumber()
        );

        log.info(
                "Verify registration OTP requested. userType={}, phone={}",
                request.userType(),
                LogMasker.maskPhone(phone.phoneNumber())
        );

        return userAccountFinder.findRequiredByPhoneAndUserType(phone, request.userType())

                .flatMap(account -> otpService.verifyOtp(
                                phone.countryCode(),
                                phone.phoneNumber(),
                                OtpPurpose.REGISTRATION,
                                request.otpCode()
                        )
                        .then(userAccountStateService.markOtpVerified(account)))

                .flatMap(account -> registrationSessionService.createSession(
                                account.getId(),
                                account.getUserType(),
                                account.getRegistrationStatus()
                        )
                        .map(registrationToken -> registrationStatusMapper.toResponse(
                                account,
                                registrationToken
                        )))

                .doOnSuccess(response -> log.info(
                        "Verify registration OTP completed. userType={}, phone={}, status={}, nextStep={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        response.registrationStatus(),
                        response.nextStep()
                ))

                .doOnError(error -> log.warn(
                        "Verify registration OTP failed. userType={}, phone={}, reason={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        error.getMessage()
                ));
    }
}
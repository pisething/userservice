package com.pisethjavaschool.userservice.user.facade.registration.impl;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.domain.enumeration.OtpPurpose;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;
import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneRequest;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneResponse;
import com.pisethjavaschool.userservice.user.facade.registration.RegisterPhoneFacade;
import com.pisethjavaschool.userservice.user.mapper.RegisterPhoneResponseMapper;
import com.pisethjavaschool.userservice.user.service.OtpService;
import com.pisethjavaschool.userservice.user.service.PhoneNumberService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.service.UserRegistrationCreator;
import com.pisethjavaschool.userservice.user.util.LogMasker;
import com.pisethjavaschool.userservice.user.validation.UserAccountRegistrationValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterPhoneFacadeImpl implements RegisterPhoneFacade {

    private static final String REGISTRATION_RESUMED_MESSAGE = "Registration resumed. OTP sent.";
    private static final String OTP_SENT_MESSAGE = "OTP sent.";

    private final PhoneNumberService phoneNumberService;
    private final UserAccountFinder userAccountFinder;
    private final UserRegistrationCreator userRegistrationCreator;
    private final OtpService otpService;
    private final UserAccountRegistrationValidator registrationValidator;
    private final RegisterPhoneResponseMapper registerPhoneResponseMapper;

    @Override
    public Mono<RegisterPhoneResponse> execute(RegisterPhoneRequest request) {
        log.info(
                "Register phone requested. userType={}, phone={}",
                request.userType(),
                LogMasker.maskPhone(request.phoneNumber())
        );

        /*
         * Reason:
         * Normalize phone once at the beginning.
         * Other classes should work with a clean and consistent phone format.
         */
        NormalizedPhone phone = phoneNumberService.normalize(
                request.countryCode(),
                request.phoneNumber()
        );

        return userAccountFinder.findByPhoneAndUserType(phone, request.userType())
                .flatMap(existingAccount -> resumeRegistration(existingAccount, phone))
                .switchIfEmpty(createRegistration(phone, request.userType()))
                .doOnSuccess(response -> log.info(
                        "Register phone completed. userType={}, phone={}, registrationStatus={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        response.registrationStatus()
                ))
                .doOnError(error -> log.warn(
                        "Register phone failed. userType={}, phone={}, reason={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        error.getMessage()
                ));
    }

    private Mono<RegisterPhoneResponse> resumeRegistration(
            UserAccount account,
            NormalizedPhone phone
    ) {
        /*
         * Reason:
         * Existing account may continue registration only if it is not already completed.
         */
        return registrationValidator.validateCanRegisterOrResume(account)
                .then(otpService.sendOtp(
                        phone.countryCode(),
                        phone.phoneNumber(),
                        OtpPurpose.REGISTRATION
                ))
                .thenReturn(registerPhoneResponseMapper.toResponse(
                        account,
                        REGISTRATION_RESUMED_MESSAGE
                ));
    }

    private Mono<RegisterPhoneResponse> createRegistration(
            NormalizedPhone phone,
            UserType userType
    ) {
        /*
         * Reason:
         * New registration creation is delegated to UserRegistrationCreator.
         * This keeps account creation logic reusable and separate.
         */
        return userRegistrationCreator.create(phone, userType)
                .map(account -> registerPhoneResponseMapper.toResponse(
                        account,
                        OTP_SENT_MESSAGE
                ));
    }
}
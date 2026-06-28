package com.pisethjavaschool.userservice.user.facade.registration.impl;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.enumeration.OtpPurpose;
import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneRequest;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneResponse;
import com.pisethjavaschool.userservice.user.facade.registration.ResendRegistrationOtpFacade;
import com.pisethjavaschool.userservice.user.mapper.RegisterPhoneResponseMapper;
import com.pisethjavaschool.userservice.user.service.OtpService;
import com.pisethjavaschool.userservice.user.service.PhoneNumberService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.util.LogMasker;
import com.pisethjavaschool.userservice.user.validation.UserAccountRegistrationValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResendRegistrationOtpFacadeImpl implements ResendRegistrationOtpFacade {

    private static final String OTP_RESENT_MESSAGE = "OTP resent.";

    private final PhoneNumberService phoneNumberService;
    private final UserAccountFinder userAccountFinder;
    private final UserAccountRegistrationValidator registrationValidator;
    private final OtpService otpService;
    private final RegisterPhoneResponseMapper registerPhoneResponseMapper;

    @Override
    public Mono<RegisterPhoneResponse> execute(RegisterPhoneRequest request) {
        NormalizedPhone phone = phoneNumberService.normalize(
                request.countryCode(),
                request.phoneNumber()
        );

        log.info(
                "Resend registration OTP requested. userType={}, phone={}",
                request.userType(),
                LogMasker.maskPhone(phone.phoneNumber())
        );

        return userAccountFinder.findRequiredByPhoneAndUserType(
                        phone,
                        request.userType()
                )

                /*
                 * Reason:
                 * Only users who are still inside registration flow
                 * should be allowed to request registration OTP again.
                 *
                 * If account is already completed, resend registration OTP is not allowed.
                 */
                .flatMap(account -> registrationValidator.validateCanResendRegistrationOtp(account)
                        .then(otpService.sendOtp(
                                phone.countryCode(),
                                phone.phoneNumber(),
                                OtpPurpose.REGISTRATION
                        ))
                        .thenReturn(registerPhoneResponseMapper.toResponse(
                                account,
                                OTP_RESENT_MESSAGE
                        )))

                .doOnSuccess(response -> log.info(
                        "Resend registration OTP completed. userType={}, phone={}, registrationStatus={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        response.registrationStatus()
                ))

                .doOnError(error -> log.warn(
                        "Resend registration OTP failed. userType={}, phone={}, reason={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        error.getMessage()
                ));
    }
}

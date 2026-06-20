package com.pisethjavaschool.userservice.user.facade.registration.impl;

import com.pisethjavaschool.userservice.user.domain.enumeration.OtpPurpose;
import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.dto.RegistrationStatusResponse;
import com.pisethjavaschool.userservice.user.dto.VerifyOtpRequest;
import com.pisethjavaschool.userservice.user.facade.registration.VerifyRegistrationOtpFacade;
import com.pisethjavaschool.userservice.user.mapper.RegistrationStatusMapper;
import com.pisethjavaschool.userservice.user.service.OtpService;
import com.pisethjavaschool.userservice.user.service.PhoneNumberService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.service.UserAccountStateService;
import com.pisethjavaschool.userservice.user.util.LogMasker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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

    @Override
    public Mono<RegistrationStatusResponse> execute(VerifyOtpRequest request) {
        /*
         * Reason:
         * Phone normalization is the first step because the database stores phone
         * in normalized format.
         */
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

                /*
                 * Reason:
                 * OTP verification and account state update are part of one workflow.
                 * The Facade controls the workflow, but the real work is delegated
                 * to smaller services.
                 */
                .flatMap(account -> otpService.verifyOtp(
                                phone.countryCode(),
                                phone.phoneNumber(),
                                OtpPurpose.REGISTRATION,
                                request.otpCode()
                        )
                        .then(userAccountStateService.markOtpVerified(account)))

                /*
                 * Reason:
                 * Mapper is responsible only for converting entity to response.
                 * This keeps response-building logic out of the Facade.
                 */
                .map(registrationStatusMapper::toResponse)

                .doOnSuccess(response -> log.info(
                        "Verify registration OTP completed. userType={}, phone={}, status={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        response.registrationStatus()
                ))

                .doOnError(error -> log.warn(
                        "Verify registration OTP failed. userType={}, phone={}, reason={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        error.getMessage()
                ));
    }
}
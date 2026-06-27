package com.pisethjavaschool.userservice.user.facade.registration.impl;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneRequest;
import com.pisethjavaschool.userservice.user.dto.RegistrationStatusResponse;
import com.pisethjavaschool.userservice.user.facade.registration.ResumeRegistrationFacade;
import com.pisethjavaschool.userservice.user.mapper.RegistrationStatusMapper;
import com.pisethjavaschool.userservice.user.service.PhoneNumberService;
import com.pisethjavaschool.userservice.user.service.RegistrationSessionService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.util.LogMasker;
import com.pisethjavaschool.userservice.user.validation.UserAccountRegistrationValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResumeRegistrationFacadeImpl implements ResumeRegistrationFacade {

    private final PhoneNumberService phoneNumberService;
    private final UserAccountFinder userAccountFinder;
    private final UserAccountRegistrationValidator registrationValidator;
    private final RegistrationSessionService registrationSessionService;
    private final RegistrationStatusMapper registrationStatusMapper;

    @Override
    public Mono<RegistrationStatusResponse> execute(RegisterPhoneRequest request) {
        NormalizedPhone phone = phoneNumberService.normalize(
                request.countryCode(),
                request.phoneNumber()
        );

        log.info(
                "Resume registration requested. userType={}, phone={}",
                request.userType(),
                LogMasker.maskPhone(phone.phoneNumber())
        );

        return userAccountFinder.findRequiredByPhoneAndUserType(
                        phone,
                        request.userType()
                )
                .flatMap(account -> registrationValidator.validateCanResumeRegistration(account)
                        .then(registrationSessionService.createSession(
                                account.getId(),
                                account.getUserType(),
                                account.getRegistrationStatus()
                        ))
                        .map(registrationToken -> registrationStatusMapper.toResponse(
                                account,
                                registrationToken
                        )))
                .doOnSuccess(response -> log.info(
                        "Resume registration completed. userType={}, phone={}, registrationStatus={}, nextStep={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        response.registrationStatus(),
                        response.nextStep()
                ))
                .doOnError(error -> log.warn(
                        "Resume registration failed. userType={}, phone={}, reason={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        error.getMessage()
                ));
    }
}
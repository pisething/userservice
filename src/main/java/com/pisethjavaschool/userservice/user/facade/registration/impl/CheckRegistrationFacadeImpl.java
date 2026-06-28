package com.pisethjavaschool.userservice.user.facade.registration.impl;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneRequest;
import com.pisethjavaschool.userservice.user.dto.RegistrationStatusResponse;
import com.pisethjavaschool.userservice.user.facade.registration.CheckRegistrationFacade;
import com.pisethjavaschool.userservice.user.mapper.RegistrationStatusMapper;
import com.pisethjavaschool.userservice.user.service.PhoneNumberService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.util.LogMasker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckRegistrationFacadeImpl implements CheckRegistrationFacade {

    private final PhoneNumberService phoneNumberService;
    private final UserAccountFinder userAccountFinder;
    private final RegistrationStatusMapper registrationStatusMapper;

    @Override
    public Mono<RegistrationStatusResponse> execute(RegisterPhoneRequest request) {
        NormalizedPhone phone = phoneNumberService.normalize(
                request.countryCode(),
                request.phoneNumber()
        );

        log.info(
                "Check registration requested. userType={}, phone={}",
                request.userType(),
                LogMasker.maskPhone(phone.phoneNumber())
        );

        return userAccountFinder.findByPhoneAndUserType(phone, request.userType())

                .map(registrationStatusMapper::toResponse)

                .defaultIfEmpty(registrationStatusMapper.notRegistered(request.userType()))

                .doOnSuccess(response -> log.info(
                        "Check registration completed. userType={}, phone={}, exists={}, nextStep={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        response.exists(),
                        response.nextStep()
                ))

                .doOnError(error -> log.warn(
                        "Check registration failed. userType={}, phone={}, reason={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        error.getMessage()
                ));
    }

}
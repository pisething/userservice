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
        /*
         * Reason:
         * Always normalize phone before searching.
         * User may input phone as 078..., 78..., or with spaces.
         */
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

                /*
                 * Reason:
                 * If account exists, return its real registration status.
                 */
                .map(registrationStatusMapper::toResponse)

                /*
                 * Reason:
                 * If account does not exist, return NOT_REGISTERED response.
                 */
                .defaultIfEmpty(registrationStatusMapper.notRegistered(request.userType()));
    }
}
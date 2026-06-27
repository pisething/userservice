package com.pisethjavaschool.userservice.user.facade.registration.impl;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.enumeration.RegistrationStatus;
import com.pisethjavaschool.userservice.user.dto.SetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;
import com.pisethjavaschool.userservice.user.exception.InvalidRegistrationTokenException;
import com.pisethjavaschool.userservice.user.facade.registration.SetRegistrationPinFacade;
import com.pisethjavaschool.userservice.user.mapper.UserAccountMapper;
import com.pisethjavaschool.userservice.user.service.CustomerProfileService;
import com.pisethjavaschool.userservice.user.service.RegistrationSessionService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.service.UserAccountStateService;
import com.pisethjavaschool.userservice.user.service.UserIdentityProvisioningService;
import com.pisethjavaschool.userservice.user.validation.PinValidator;
import com.pisethjavaschool.userservice.user.validation.UserAccountRegistrationValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class SetRegistrationPinFacadeImpl implements SetRegistrationPinFacade {

    private final PinValidator pinValidator;
    private final UserAccountFinder userAccountFinder;
    private final UserAccountRegistrationValidator registrationValidator;
    private final CustomerProfileService customerProfileService;
    private final UserIdentityProvisioningService userIdentityProvisioningService;
    private final UserAccountStateService userAccountStateService;
    private final UserAccountMapper userAccountMapper;
    private final RegistrationSessionService registrationSessionService;

    @Override
    public Mono<UserAccountResponse> execute(
            UUID userAccountId,
            String registrationToken,
            SetPinRequest request
    ) {
        log.info("Set registration PIN requested. userAccountId={}", userAccountId);

        pinValidator.validateMatched(request.pin(), request.confirmPin());

        return registrationSessionService.getRequiredSessionForUser(
                        registrationToken,
                        userAccountId
                )

                .flatMap(session -> {
                    if (session.registrationStatus() != RegistrationStatus.PROFILE_COMPLETED) {
                        return Mono.error(new InvalidRegistrationTokenException());
                    }

                    return userAccountFinder.findRequiredById(userAccountId);
                })

                .flatMap(account -> registrationValidator.validateCanSetPin(account)

                        .then(customerProfileService.findRequiredByUserAccountId(account.getId()))

                        .flatMap(profile -> userIdentityProvisioningService.provisionOrResetPin(
                                account,
                                profile,
                                request.pin()
                        )))

                .flatMap(userAccountStateService::activate)

                .flatMap(account -> registrationSessionService.deleteSession(registrationToken)
                        .thenReturn(account))

                .map(userAccountMapper::toResponse)

                .doOnSuccess(response -> log.info(
                        "Set registration PIN completed. userAccountId={}",
                        userAccountId
                ))

                .doOnError(error -> log.warn(
                        "Set registration PIN failed. userAccountId={}, reason={}",
                        userAccountId,
                        error.getMessage()
                ));
    }
}
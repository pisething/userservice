package com.pisethjavaschool.userservice.user.facade.registration.impl;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.enumeration.RegistrationStatus;
import com.pisethjavaschool.userservice.user.dto.CustomerProfileRequest;
import com.pisethjavaschool.userservice.user.dto.CustomerProfileResponse;
import com.pisethjavaschool.userservice.user.exception.InvalidRegistrationTokenException;
import com.pisethjavaschool.userservice.user.facade.registration.CompleteCustomerProfileFacade;
import com.pisethjavaschool.userservice.user.mapper.CustomerProfileResponseMapper;
import com.pisethjavaschool.userservice.user.service.CustomerProfileService;
import com.pisethjavaschool.userservice.user.service.RegistrationSessionService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.service.UserAccountStateService;
import com.pisethjavaschool.userservice.user.validation.UserAccountRegistrationValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompleteCustomerProfileFacadeImpl implements CompleteCustomerProfileFacade {

    private final UserAccountFinder userAccountFinder;
    private final UserAccountRegistrationValidator registrationValidator;
    private final CustomerProfileService customerProfileService;
    private final UserAccountStateService userAccountStateService;
    private final CustomerProfileResponseMapper customerProfileResponseMapper;
    private final RegistrationSessionService registrationSessionService;

    @Override
    public Mono<CustomerProfileResponse> execute(
            UUID userAccountId,
            String registrationToken,
            CustomerProfileRequest request
    ) {
        log.info("Complete customer profile requested. userAccountId={}", userAccountId);

        return registrationSessionService.getRequiredSessionForUser(
                        registrationToken,
                        userAccountId
                )

                .flatMap(session -> {
                    if (session.registrationStatus() != RegistrationStatus.OTP_VERIFIED
                            && session.registrationStatus() != RegistrationStatus.PROFILE_COMPLETED) {
                        return Mono.error(new InvalidRegistrationTokenException());
                    }

                    return userAccountFinder.findRequiredById(userAccountId);
                })

                .flatMap(account -> registrationValidator.validateCanCompleteCustomerProfile(account)

                        .then(customerProfileService.upsert(account.getId(), request))

                        .flatMap(profile -> userAccountStateService.markProfileCompleted(account)
                                .then(registrationSessionService.updateStatus(
                                        registrationToken,
                                        RegistrationStatus.PROFILE_COMPLETED
                                ))
                                .thenReturn(profile)))

                .map(customerProfileResponseMapper::toResponse)

                .doOnSuccess(response -> log.info(
                        "Complete customer profile completed. userAccountId={}",
                        userAccountId
                ))

                .doOnError(error -> log.warn(
                        "Complete customer profile failed. userAccountId={}, reason={}",
                        userAccountId,
                        error.getMessage()
                ));
    }
}
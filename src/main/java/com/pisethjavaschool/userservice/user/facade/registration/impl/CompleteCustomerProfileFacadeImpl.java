package com.pisethjavaschool.userservice.user.facade.registration.impl;

import com.pisethjavaschool.userservice.user.dto.CustomerProfileRequest;
import com.pisethjavaschool.userservice.user.dto.CustomerProfileResponse;
import com.pisethjavaschool.userservice.user.facade.registration.CompleteCustomerProfileFacade;
import com.pisethjavaschool.userservice.user.mapper.CustomerProfileResponseMapper;
import com.pisethjavaschool.userservice.user.service.CustomerProfileService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.service.UserAccountStateService;
import com.pisethjavaschool.userservice.user.validation.UserAccountRegistrationValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CompleteCustomerProfileFacadeImpl implements CompleteCustomerProfileFacade {

    private final UserAccountFinder userAccountFinder;
    private final UserAccountRegistrationValidator registrationValidator;
    private final CustomerProfileService customerProfileService;
    private final UserAccountStateService userAccountStateService;
    private final CustomerProfileResponseMapper customerProfileResponseMapper;

    @Override
    public Mono<CustomerProfileResponse> execute(
            UUID userAccountId,
            CustomerProfileRequest request
    ) {
        log.info("Complete customer profile requested. userAccountId={}", userAccountId);

        return userAccountFinder.findRequiredById(userAccountId)

                /*
                 * Reason:
                 * Before completing profile, we must validate account state.
                 * Example: user should verify OTP first.
                 */
                .flatMap(account -> registrationValidator.validateCanCompleteCustomerProfile(account)

                        /*
                         * Reason:
                         * CustomerProfileService handles profile persistence.
                         * Facade only controls the registration workflow.
                         */
                        .then(customerProfileService.upsert(account.getId(), request))

                        /*
                         * Reason:
                         * After profile is saved, account registration status should move forward.
                         * This state change belongs to UserAccountStateService.
                         */
                        .flatMap(profile -> userAccountStateService.markProfileCompleted(account)
                                .thenReturn(profile)))

                /*
                 * Reason:
                 * Mapper converts internal entity/model to API response.
                 */
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
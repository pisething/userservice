package com.pisethjavaschool.userservice.user.facade.registration.impl;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.dto.SetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;
import com.pisethjavaschool.userservice.user.facade.registration.SetRegistrationPinFacade;
import com.pisethjavaschool.userservice.user.mapper.UserAccountMapper;
import com.pisethjavaschool.userservice.user.service.CustomerProfileService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.service.UserAccountStateService;
import com.pisethjavaschool.userservice.user.service.UserIdentityProvisioningService;
import com.pisethjavaschool.userservice.user.validation.PinValidator;
import com.pisethjavaschool.userservice.user.validation.UserAccountRegistrationValidator;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

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

    @Override
    public Mono<UserAccountResponse> execute(UUID userAccountId, SetPinRequest request) {

        /*
         * Reason:
         * PIN confirmation is a simple validation.
         * Your project already has PinValidator, so we reuse it.
         */
        pinValidator.validateMatched(request.pin(), request.confirmPin());

        return userAccountFinder.findRequiredById(userAccountId)

                /*
                 * Reason:
                 * RegistrationValidator protects the registration flow.
                 * User cannot set PIN before OTP/profile step is completed.
                 */
                .flatMap(account -> registrationValidator.validateCanSetPin(account)

                        /*
                         * Reason:
                         * Profile is required because provisioning identity needs both:
                         * - UserAccount
                         * - CustomerProfile
                         */
                        .then(customerProfileService.findRequiredByUserAccountId(account.getId()))

                        /*
                         * Reason:
                         * UserIdentityProvisioningService owns Keycloak / identity logic.
                         * Facade should not know how PIN is stored or reset.
                         */
                        .flatMap(profile -> userIdentityProvisioningService.provisionOrResetPin(
                                account,
                                profile,
                                request.pin()
                        )))

                /*
                 * Reason:
                 * After PIN is created successfully, account becomes ACTIVE.
                 */
                .flatMap(userAccountStateService::activate)

                /*
                 * Reason:
                 * Your project uses UserAccountMapper, not UserAccountResponseMapper.
                 */
                .map(userAccountMapper::toResponse);
    }
}
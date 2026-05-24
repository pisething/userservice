package com.pisethjavaschool.userservice.user.service.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.pisethjavaschool.userservice.user.domain.CustomerProfile;
import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.service.UserIdentityProvisioningService;
import com.pisethjavaschool.userservice.user.service.keycloak.KeycloakAdminClient;
import com.pisethjavaschool.userservice.user.service.keycloak.dto.KeycloakCreateUserRequest;
import com.pisethjavaschool.userservice.user.service.keycloak.dto.KeycloakResetPasswordRequest;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserIdentityProvisioningServiceImpl implements UserIdentityProvisioningService {

    private final KeycloakAdminClient keycloakAdminClient;

    @Override
    public Mono<UserAccount> provisionOrResetPin(UserAccount account, CustomerProfile profile, String pin) {
        if (hasKeycloakUser(account)) {
            return keycloakAdminClient.resetPassword(toResetPasswordRequest(account, pin))
                    .thenReturn(account);
        }

        return keycloakAdminClient.createUser(toCreateUserRequest(account, profile, pin))
                .map(keycloakUserId -> {
                    account.setKeycloakUserId(keycloakUserId);
                    return account;
                });
    }

    private KeycloakCreateUserRequest toCreateUserRequest(
            UserAccount account,
            CustomerProfile profile,
            String pin
    ) {
        return new KeycloakCreateUserRequest(
                buildUsername(account),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getEmail(),
                pin,
                Map.of(
                        "userAccountId", account.getId().toString(),
                        "userType", account.getUserType().name(),
                        "phoneNumber", buildUsername(account)
                )
        );
    }

    private KeycloakResetPasswordRequest toResetPasswordRequest(UserAccount account, String pin) {
        return new KeycloakResetPasswordRequest(
                account.getKeycloakUserId(),
                pin,
                false
        );
    }

    private String buildUsername(UserAccount account) {
        return account.getCountryCode() + account.getPhoneNumber();
    }

    private boolean hasKeycloakUser(UserAccount account) {
        return account.getKeycloakUserId() != null && !account.getKeycloakUserId().isBlank();
    }
}
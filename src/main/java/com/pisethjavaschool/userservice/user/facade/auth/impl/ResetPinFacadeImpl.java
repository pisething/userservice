package com.pisethjavaschool.userservice.user.facade.auth.impl;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.dto.ResetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;
import com.pisethjavaschool.userservice.user.facade.auth.ResetPinFacade;
import com.pisethjavaschool.userservice.user.mapper.UserAccountMapper;
import com.pisethjavaschool.userservice.user.repository.UserAccountRepository;
import com.pisethjavaschool.userservice.user.service.ResetPinSessionService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.service.keycloak.KeycloakAdminClient;
import com.pisethjavaschool.userservice.user.service.keycloak.dto.KeycloakResetPasswordRequest;
import com.pisethjavaschool.userservice.user.validation.PinValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResetPinFacadeImpl implements ResetPinFacade {

    private final PinValidator pinValidator;
    private final ResetPinSessionService resetPinSessionService;
    private final UserAccountFinder userAccountFinder;
    private final KeycloakAdminClient keycloakAdminClient;
    private final UserAccountRepository userAccountRepository;
    private final UserAccountMapper userAccountMapper;

    @Override
    public Mono<UserAccountResponse> execute(String resetToken, ResetPinRequest request) {
        pinValidator.validateMatched(request.pin(), request.confirmPin());

        log.info("Reset PIN requested.");

        return resetPinSessionService.getRequiredSession(resetToken)
                .flatMap(session -> userAccountFinder.findRequiredById(session.userAccountId())
                        .flatMap(account -> keycloakAdminClient.resetPassword(
                                        new KeycloakResetPasswordRequest(
                                                account.getKeycloakUserId(),
                                                request.pin(),
                                                false
                                        )
                                )
                                .then(updateLastModifiedAt(account))
                                .flatMap(updatedAccount -> resetPinSessionService.deleteSession(resetToken)
                                        .thenReturn(updatedAccount))))
                .map(userAccountMapper::toResponse)
                .doOnSuccess(response -> log.info(
                        "Reset PIN completed. userAccountId={}",
                        response.id()
                ))
                .doOnError(error -> log.warn(
                        "Reset PIN failed. reason={}",
                        error.getMessage()
                ));
    }

    private Mono<UserAccount> updateLastModifiedAt(UserAccount account) {
        account.setUpdatedAt(Instant.now());
        return userAccountRepository.save(account);
    }
}
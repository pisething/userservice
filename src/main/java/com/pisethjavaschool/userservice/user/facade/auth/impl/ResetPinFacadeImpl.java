package com.pisethjavaschool.userservice.user.facade.auth.impl;

import java.time.Instant;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.domain.enumeration.OtpPurpose;
import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.dto.ResetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;
import com.pisethjavaschool.userservice.user.facade.auth.ResetPinFacade;
import com.pisethjavaschool.userservice.user.mapper.UserAccountMapper;
import com.pisethjavaschool.userservice.user.repository.UserAccountRepository;
import com.pisethjavaschool.userservice.user.service.OtpService;
import com.pisethjavaschool.userservice.user.service.PhoneNumberService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.service.keycloak.KeycloakAdminClient;
import com.pisethjavaschool.userservice.user.service.keycloak.dto.KeycloakResetPasswordRequest;
import com.pisethjavaschool.userservice.user.util.LogMasker;
import com.pisethjavaschool.userservice.user.validation.LoginValidator;
import com.pisethjavaschool.userservice.user.validation.PinValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResetPinFacadeImpl implements ResetPinFacade {

    private final PhoneNumberService phoneNumberService;
    private final UserAccountFinder userAccountFinder;
    private final LoginValidator loginValidator;
    private final PinValidator pinValidator;
    private final OtpService otpService;
    private final KeycloakAdminClient keycloakAdminClient;
    private final UserAccountRepository userAccountRepository;
    private final UserAccountMapper userAccountMapper;

    @Override
    public Mono<UserAccountResponse> execute(ResetPinRequest request) {
        pinValidator.validateMatched(request.pin(), request.confirmPin());

        NormalizedPhone phone = phoneNumberService.normalize(
                request.countryCode(),
                request.phoneNumber()
        );

        log.info(
                "Reset PIN requested. userType={}, phone={}",
                request.userType(),
                LogMasker.maskPhone(phone.phoneNumber())
        );

        return userAccountFinder.findRequiredByPhoneAndUserType(phone, request.userType())
                .flatMap(account -> loginValidator.validateCanLogin(account).thenReturn(account))
                .flatMap(account -> otpService.verifyOtp(
                                phone.countryCode(),
                                phone.phoneNumber(),
                                OtpPurpose.FORGOT_PIN,
                                request.otpCode()
                        )
                        .then(keycloakAdminClient.resetPassword(
                                new KeycloakResetPasswordRequest(
                                        account.getKeycloakUserId(),
                                        request.pin(),
                                        false
                                )
                        ))
                        .then(updateLastModifiedAt(account)))
                .map(userAccountMapper::toResponse)
                .doOnSuccess(response -> log.info(
                        "Reset PIN completed. userType={}, phone={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber())
                ))
                .doOnError(error -> log.warn(
                        "Reset PIN failed. userType={}, phone={}, reason={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        error.getMessage()
                ));
    }

    private Mono<UserAccount> updateLastModifiedAt(UserAccount account) {
        account.setUpdatedAt(Instant.now());
        return userAccountRepository.save(account);
    }
}
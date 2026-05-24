package com.pisethjavaschool.userservice.user.service.impl;


import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.domain.enumeration.OtpPurpose;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;
import com.pisethjavaschool.userservice.user.dto.ForgotPinRequest;
import com.pisethjavaschool.userservice.user.dto.LoginRequest;
import com.pisethjavaschool.userservice.user.dto.LoginResponse;
import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.dto.ResetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;
import com.pisethjavaschool.userservice.user.mapper.UserAccountMapper;
import com.pisethjavaschool.userservice.user.repository.UserAccountRepository;
import com.pisethjavaschool.userservice.user.service.AuthService;
import com.pisethjavaschool.userservice.user.service.OtpService;
import com.pisethjavaschool.userservice.user.service.PhoneNumberService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.service.keycloak.KeycloakAdminClient;
import com.pisethjavaschool.userservice.user.service.keycloak.KeycloakAuthClient;
import com.pisethjavaschool.userservice.user.service.keycloak.dto.KeycloakResetPasswordRequest;
import com.pisethjavaschool.userservice.user.util.LogMasker;
import com.pisethjavaschool.userservice.user.util.PhoneNormalizer;
import com.pisethjavaschool.userservice.user.validation.LoginValidator;
import com.pisethjavaschool.userservice.user.validation.PinValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PhoneNumberService phoneNumberService;
    private final PhoneNormalizer phoneNormalizer;
    private final UserAccountFinder userAccountFinder;
    private final UserAccountRepository userAccountRepository;
    private final KeycloakAuthClient keycloakAuthClient;
    private final KeycloakAdminClient keycloakAdminClient;
    private final OtpService otpService;
    private final LoginValidator loginValidator;
    private final PinValidator pinValidator;
    private final UserAccountMapper userAccountMapper;

    /*
    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        NormalizedPhone phone = normalizePhone(request.countryCode(), request.phoneNumber());

        return findLoginAccount(phone, request.userType())
                .flatMap(account -> keycloakAuthClient.login(
                        phoneNormalizer.toUsername(phone.countryCode(), phone.phoneNumber()),
                        request.pin()
                ));
    }
    */
    
    @Override
    public Mono<LoginResponse> login(LoginRequest request) {
        NormalizedPhone phone = normalizePhone(request.countryCode(), request.phoneNumber());

        log.info(
                "Login requested. userType={}, phone={}",
                request.userType(),
                LogMasker.maskPhone(phone.phoneNumber())
        );

        return findLoginAccount(phone, request.userType())
                .flatMap(account -> keycloakAuthClient.login(
                        phoneNormalizer.toUsername(phone.countryCode(), phone.phoneNumber()),
                        request.pin()
                ))
                .doOnSuccess(response -> log.info(
                        "Login successful. userType={}, phone={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber())
                ))
                .doOnError(error -> log.warn(
                        "Login failed. userType={}, phone={}, reason={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        error.getMessage()
                ));
    }

    /*
    @Override
    public Mono<Void> requestForgotPinOtp(ForgotPinRequest request) {
        NormalizedPhone phone = normalizePhone(request.countryCode(), request.phoneNumber());

        return findLoginAccount(phone, request.userType())
                .then(otpService.sendOtp(
                        phone.countryCode(),
                        phone.phoneNumber(),
                        OtpPurpose.FORGOT_PIN
                ));
    }
    */
    
    @Override
    public Mono<Void> requestForgotPinOtp(ForgotPinRequest request) {
        NormalizedPhone phone = normalizePhone(request.countryCode(), request.phoneNumber());

        log.info(
                "Forgot PIN OTP requested. userType={}, phone={}",
                request.userType(),
                LogMasker.maskPhone(phone.phoneNumber())
        );

        return findLoginAccount(phone, request.userType())
                .then(otpService.sendOtp(
                        phone.countryCode(),
                        phone.phoneNumber(),
                        OtpPurpose.FORGOT_PIN
                ))
                .doOnSuccess(ignored -> log.info(
                        "Forgot PIN OTP sent. userType={}, phone={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber())
                ))
                .doOnError(error -> log.warn(
                        "Forgot PIN OTP request failed. userType={}, phone={}, reason={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        error.getMessage()
                ));
    }

    @Override
    @Transactional
    public Mono<UserAccountResponse> resetPin(ResetPinRequest request) {
        pinValidator.validateMatched(request.pin(), request.confirmPin());

        NormalizedPhone phone = normalizePhone(request.countryCode(), request.phoneNumber());

        return findLoginAccount(phone, request.userType())
                .flatMap(account -> otpService.verifyOtp(
                                phone.countryCode(),
                                phone.phoneNumber(),
                                OtpPurpose.FORGOT_PIN,
                                request.otpCode()
                        )
                        .then(keycloakAdminClient.resetPassword(toResetPasswordRequest(account, request.pin())))
                        .then(updateLastModifiedAt(account)))
                .map(userAccountMapper::toResponse);
    }

    private KeycloakResetPasswordRequest toResetPasswordRequest(UserAccount account, String pin) {
        return new KeycloakResetPasswordRequest(
                account.getKeycloakUserId(),
                pin,
                false
        );
    }

    private NormalizedPhone normalizePhone(String countryCode, String phoneNumber) {
        return phoneNumberService.normalize(countryCode, phoneNumber);
    }

    private Mono<UserAccount> findLoginAccount(NormalizedPhone phone, UserType userType) {
        return userAccountFinder.findRequiredByPhoneAndUserType(phone, userType)
                .flatMap(account -> loginValidator.validateCanLogin(account).thenReturn(account));
    }

    private Mono<UserAccount> updateLastModifiedAt(UserAccount account) {
        account.setUpdatedAt(Instant.now());

        return userAccountRepository.save(account);
    }
}
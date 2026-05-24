package com.pisethjavaschool.userservice.user.service.impl;


import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.domain.enumeration.OtpPurpose;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;
import com.pisethjavaschool.userservice.user.dto.CustomerProfileRequest;
import com.pisethjavaschool.userservice.user.dto.CustomerProfileResponse;
import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneRequest;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneResponse;
import com.pisethjavaschool.userservice.user.dto.RegistrationStatusResponse;
import com.pisethjavaschool.userservice.user.dto.SetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;
import com.pisethjavaschool.userservice.user.dto.VerifyOtpRequest;
import com.pisethjavaschool.userservice.user.mapper.CustomerProfileResponseMapper;
import com.pisethjavaschool.userservice.user.mapper.RegisterPhoneResponseMapper;
import com.pisethjavaschool.userservice.user.mapper.RegistrationStatusMapper;
import com.pisethjavaschool.userservice.user.mapper.UserAccountMapper;
import com.pisethjavaschool.userservice.user.service.CustomerProfileService;
import com.pisethjavaschool.userservice.user.service.OtpService;
import com.pisethjavaschool.userservice.user.service.PhoneNumberService;
import com.pisethjavaschool.userservice.user.service.RegistrationService;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;
import com.pisethjavaschool.userservice.user.service.UserAccountStateService;
import com.pisethjavaschool.userservice.user.service.UserIdentityProvisioningService;
import com.pisethjavaschool.userservice.user.service.UserRegistrationCreator;
import com.pisethjavaschool.userservice.user.util.LogMasker;
import com.pisethjavaschool.userservice.user.validation.PinValidator;
import com.pisethjavaschool.userservice.user.validation.UserAccountRegistrationValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private static final String REGISTRATION_RESUMED_MESSAGE = "Registration resumed. OTP sent.";
    private static final String OTP_SENT_MESSAGE = "OTP sent.";

    private final PhoneNumberService phoneNumberService;
    private final OtpService otpService;
    private final UserAccountFinder userAccountFinder;
    private final UserRegistrationCreator userRegistrationCreator;
    private final CustomerProfileService customerProfileService;
    private final UserAccountStateService userAccountStateService;
    private final UserIdentityProvisioningService userIdentityProvisioningService;
    private final UserAccountRegistrationValidator registrationValidator;
    private final PinValidator pinValidator;
    private final RegistrationStatusMapper registrationStatusMapper;
    private final UserAccountMapper userAccountMapper;
    private final CustomerProfileResponseMapper customerProfileResponseMapper;
    private final RegisterPhoneResponseMapper registerPhoneResponseMapper;

    @Override
    @Transactional
    public Mono<RegisterPhoneResponse> registerPhone(RegisterPhoneRequest request) {
    	log.info(
    	        "Register phone requested. userType={}, phone={}",
    	        request.userType(),
    	        LogMasker.maskPhone(request.phoneNumber())
    	);
        NormalizedPhone phone = phoneNumberService.normalize(
                request.countryCode(),
                request.phoneNumber()
        );
        /*
        return userAccountFinder.findByPhoneAndUserType(phone, request.userType())
                .flatMap(existingAccount -> resumeRegistration(existingAccount, phone))
                .switchIfEmpty(createRegistration(phone, request.userType()));
                */
        return userAccountFinder.findByPhoneAndUserType(phone, request.userType())
                .flatMap(existingAccount -> resumeRegistration(existingAccount, phone))
                .switchIfEmpty(createRegistration(phone, request.userType()))
                .doOnSuccess(response -> log.info(
                        "Register phone completed. userType={}, phone={}, registrationStatus={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        response.registrationStatus()
                ))
                .doOnError(error -> log.warn(
                        "Register phone failed. userType={}, phone={}, reason={}",
                        request.userType(),
                        LogMasker.maskPhone(phone.phoneNumber()),
                        error.getMessage()
                ));
    }

    @Override
    @Transactional
    public Mono<RegistrationStatusResponse> verifyOtp(VerifyOtpRequest request) {
        NormalizedPhone phone = phoneNumberService.normalize(
                request.countryCode(),
                request.phoneNumber()
        );

        return userAccountFinder.findRequiredByPhoneAndUserType(phone, request.userType())
                .flatMap(account -> otpService.verifyOtp(
                                phone.countryCode(),
                                phone.phoneNumber(),
                                OtpPurpose.REGISTRATION,
                                request.otpCode()
                        )
                        .then(userAccountStateService.markOtpVerified(account)))
                .map(registrationStatusMapper::toResponse);
    }

    @Override
    @Transactional
    public Mono<CustomerProfileResponse> completeCustomerProfile(
            UUID userAccountId,
            CustomerProfileRequest request
    ) {
        return userAccountFinder.findRequiredById(userAccountId)
                .flatMap(account -> registrationValidator.validateCanCompleteCustomerProfile(account)
                        .then(customerProfileService.upsert(account.getId(), request))
                        .flatMap(profile -> userAccountStateService.markProfileCompleted(account)
                                .thenReturn(profile)))
                .map(customerProfileResponseMapper::toResponse);
    }

    @Override
    @Transactional
    public Mono<UserAccountResponse> setPin(UUID userAccountId, SetPinRequest request) {
        pinValidator.validateMatched(request.pin(), request.confirmPin());

        return userAccountFinder.findRequiredById(userAccountId)
                .flatMap(account -> registrationValidator.validateCanSetPin(account)
                        .then(customerProfileService.findRequiredByUserAccountId(account.getId()))
                        .flatMap(profile -> userIdentityProvisioningService.provisionOrResetPin(
                                account,
                                profile,
                                request.pin()
                        )))
                .flatMap(userAccountStateService::activate)
                .map(userAccountMapper::toResponse);
    }

    @Override
    public Mono<RegistrationStatusResponse> checkRegistration(RegisterPhoneRequest request) {
        NormalizedPhone phone = phoneNumberService.normalize(
                request.countryCode(),
                request.phoneNumber()
        );

        return userAccountFinder.findByPhoneAndUserType(phone, request.userType())
                .map(registrationStatusMapper::toResponse)
                .defaultIfEmpty(registrationStatusMapper.notRegistered(request.userType()));
    }

    private Mono<RegisterPhoneResponse> resumeRegistration(
            UserAccount account,
            NormalizedPhone phone
    ) {
        return registrationValidator.validateCanRegisterOrResume(account)
                .then(otpService.sendOtp(
                        phone.countryCode(),
                        phone.phoneNumber(),
                        OtpPurpose.REGISTRATION
                ))
                .thenReturn(registerPhoneResponseMapper.toResponse(
                        account,
                        REGISTRATION_RESUMED_MESSAGE
                ));
    }

    private Mono<RegisterPhoneResponse> createRegistration(
            NormalizedPhone phone,
            UserType userType
    ) {
        return userRegistrationCreator.create(phone, userType)
                .map(account -> registerPhoneResponseMapper.toResponse(
                        account,
                        OTP_SENT_MESSAGE
                ));
    }
}
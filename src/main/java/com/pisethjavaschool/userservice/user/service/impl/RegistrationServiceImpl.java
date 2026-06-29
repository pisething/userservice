package com.pisethjavaschool.userservice.user.service.impl;


import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pisethjavaschool.userservice.user.dto.CustomerProfileRequest;
import com.pisethjavaschool.userservice.user.dto.CustomerProfileResponse;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneRequest;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneResponse;
import com.pisethjavaschool.userservice.user.dto.RegistrationStatusResponse;
import com.pisethjavaschool.userservice.user.dto.SetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;
import com.pisethjavaschool.userservice.user.dto.VerifyOtpRequest;
import com.pisethjavaschool.userservice.user.facade.registration.CheckRegistrationFacade;
import com.pisethjavaschool.userservice.user.facade.registration.CompleteCustomerProfileFacade;
import com.pisethjavaschool.userservice.user.facade.registration.RegisterPhoneFacade;
import com.pisethjavaschool.userservice.user.facade.registration.ResendRegistrationOtpFacade;
import com.pisethjavaschool.userservice.user.facade.registration.ResumeRegistrationFacade;
import com.pisethjavaschool.userservice.user.facade.registration.SetRegistrationPinFacade;
import com.pisethjavaschool.userservice.user.facade.registration.VerifyRegistrationOtpFacade;
import com.pisethjavaschool.userservice.user.service.RegistrationService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final RegisterPhoneFacade registerPhoneFacade;
    private final VerifyRegistrationOtpFacade verifyRegistrationOtpFacade;
    private final CompleteCustomerProfileFacade completeCustomerProfileFacade;
    private final SetRegistrationPinFacade setRegistrationPinFacade;
    private final CheckRegistrationFacade checkRegistrationFacade;
    private final ResendRegistrationOtpFacade resendRegistrationOtpFacade;
    private final ResumeRegistrationFacade resumeRegistrationFacade;

    @Override
    @Transactional
    public Mono<RegisterPhoneResponse> registerPhone(RegisterPhoneRequest request) {
    	return registerPhoneFacade.execute(request);
    }

    @Override
    @Transactional
    public Mono<RegistrationStatusResponse> verifyOtp(VerifyOtpRequest request) {
        return verifyRegistrationOtpFacade.execute(request);
    }

    @Override
    @Transactional
    public Mono<CustomerProfileResponse> completeCustomerProfile(
            UUID userAccountId,
            String registrationToken,
            CustomerProfileRequest request
    ) {
        return completeCustomerProfileFacade.execute(userAccountId, registrationToken, request);
    }

    @Override
    @Transactional
    public Mono<UserAccountResponse> setPin(UUID userAccountId, String registrationToken, SetPinRequest request) {
        return setRegistrationPinFacade.execute(userAccountId, registrationToken, request);
    }

    @Override
    public Mono<RegistrationStatusResponse> checkRegistration(RegisterPhoneRequest request) {
        return checkRegistrationFacade.execute(request);
    }
    
    @Override
    @Transactional
    public Mono<RegisterPhoneResponse> resendOtp(RegisterPhoneRequest request) {
        /*
         * Reason:
         * Resend OTP is a separate use case from register phone.
         * Register phone may create a new account.
         * Resend OTP must only work for an existing registration.
         */
        return resendRegistrationOtpFacade.execute(request);
    }
    
    @Override
    @Transactional
    public Mono<RegistrationStatusResponse> resumeRegistration(RegisterPhoneRequest request) {
        return resumeRegistrationFacade.execute(request);
    }

    
}
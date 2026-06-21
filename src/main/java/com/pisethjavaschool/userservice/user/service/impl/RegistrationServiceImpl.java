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


    @Override
    @Transactional
    public Mono<RegisterPhoneResponse> registerPhone(RegisterPhoneRequest request) {
        /*
         * Reason:
         * Service layer becomes thin.
         * It does not know the registration steps anymore.
         * It only delegates to the correct Facade.
         */
        return registerPhoneFacade.execute(request);
    }

    @Override
    @Transactional
    public Mono<RegistrationStatusResponse> verifyOtp(VerifyOtpRequest request) {
        /*
         * Reason:
         * RegistrationServiceImpl should not control OTP verification details.
         * It delegates the complete use case to the Facade.
         */
        return verifyRegistrationOtpFacade.execute(request);
    }

    @Override
    @Transactional
    public Mono<CustomerProfileResponse> completeCustomerProfile(
            UUID userAccountId,
            String registrationToken,
            CustomerProfileRequest request
    ) {
        return completeCustomerProfileFacade.execute(
                userAccountId,
                registrationToken,
                request
        );
    }

    @Override
    @Transactional
    public Mono<UserAccountResponse> setPin(
            UUID userAccountId,
            String registrationToken,
            SetPinRequest request
    ) {
        return setRegistrationPinFacade.execute(
                userAccountId,
                registrationToken,
                request
        );
    }

    @Override
    public Mono<RegistrationStatusResponse> checkRegistration(RegisterPhoneRequest request) {
        /*
         * Reason:
         * Service delegates the workflow to Facade.
         * Service does not need to know how registration status is checked.
         */
        return checkRegistrationFacade.execute(request);
    }

    
}
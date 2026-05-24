package com.pisethjavaschool.userservice.user.service;

import java.util.UUID;

import com.pisethjavaschool.userservice.user.dto.CustomerProfileRequest;
import com.pisethjavaschool.userservice.user.dto.CustomerProfileResponse;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneRequest;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneResponse;
import com.pisethjavaschool.userservice.user.dto.RegistrationStatusResponse;
import com.pisethjavaschool.userservice.user.dto.SetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;
import com.pisethjavaschool.userservice.user.dto.VerifyOtpRequest;

import reactor.core.publisher.Mono;

public interface RegistrationService {

    Mono<RegisterPhoneResponse> registerPhone(RegisterPhoneRequest request);

    Mono<RegistrationStatusResponse> verifyOtp(VerifyOtpRequest request);

    Mono<CustomerProfileResponse> completeCustomerProfile(UUID userAccountId, CustomerProfileRequest request);

    Mono<UserAccountResponse> setPin(UUID userAccountId, SetPinRequest request);

    Mono<RegistrationStatusResponse> checkRegistration(RegisterPhoneRequest request);
}

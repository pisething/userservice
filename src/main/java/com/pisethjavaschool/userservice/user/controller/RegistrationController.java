package com.pisethjavaschool.userservice.user.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pisethjavaschool.userservice.user.dto.CustomerProfileRequest;
import com.pisethjavaschool.userservice.user.dto.CustomerProfileResponse;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneRequest;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneResponse;
import com.pisethjavaschool.userservice.user.dto.RegistrationStatusResponse;
import com.pisethjavaschool.userservice.user.dto.SetPinRequest;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;
import com.pisethjavaschool.userservice.user.dto.VerifyOtpRequest;
import com.pisethjavaschool.userservice.user.service.RegistrationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/registrations")
public class RegistrationController {

    private final RegistrationService service;

    @PostMapping("/phone")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RegisterPhoneResponse> registerPhone(@Valid @RequestBody RegisterPhoneRequest request) {
        return service.registerPhone(request);
    }

    @PostMapping("/otp/verify")
    public Mono<RegistrationStatusResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return service.verifyOtp(request);
    }

    @PostMapping("/{userAccountId}/customer-profile")
    public Mono<CustomerProfileResponse> completeCustomerProfile(
            @PathVariable UUID userAccountId,
            @Valid @RequestBody CustomerProfileRequest request
    ) {
        return service.completeCustomerProfile(userAccountId, request);
    }

    @PostMapping("/{userAccountId}/pin")
    public Mono<UserAccountResponse> setPin(@PathVariable UUID userAccountId, @Valid @RequestBody SetPinRequest request) {
        return service.setPin(userAccountId, request);
    }

    @PostMapping("/status")
    public Mono<RegistrationStatusResponse> checkRegistration(@Valid @RequestBody RegisterPhoneRequest request) {
        return service.checkRegistration(request);
    }
}

package com.pisethjavaschool.userservice.user.service.impl;

import org.springframework.stereotype.Service;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.domain.enumeration.OtpPurpose;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;
import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.factory.UserAccountFactory;
import com.pisethjavaschool.userservice.user.repository.UserAccountRepository;
import com.pisethjavaschool.userservice.user.service.OtpService;
import com.pisethjavaschool.userservice.user.service.UserRegistrationCreator;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserRegistrationCreatorImpl implements UserRegistrationCreator {

    private final UserAccountFactory userAccountFactory;
    private final UserAccountRepository repository;
    private final OtpService otpService;

    @Override
    public Mono<UserAccount> create(NormalizedPhone phone, UserType userType) {
        UserAccount account = userAccountFactory.create(
                phone.countryCode(),
                phone.phoneNumber(),
                userType
        );

        return repository.save(account)
                .flatMap(savedAccount -> otpService.sendOtp(
                                phone.countryCode(),
                                phone.phoneNumber(),
                                OtpPurpose.REGISTRATION
                        )
                        .thenReturn(savedAccount));
    }
}
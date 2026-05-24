package com.pisethjavaschool.userservice.user.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;
import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.exception.NotFoundException;
import com.pisethjavaschool.userservice.user.repository.UserAccountRepository;
import com.pisethjavaschool.userservice.user.service.UserAccountFinder;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserAccountFinderImpl implements UserAccountFinder {

    private final UserAccountRepository repository;

    @Override
    public Mono<UserAccount> findRequiredById(UUID userAccountId) {
        return repository.findById(userAccountId)
                .switchIfEmpty(Mono.error(new NotFoundException("User account not found")));
    }

    @Override
    public Mono<UserAccount> findRequiredByPhoneAndUserType(NormalizedPhone phone, UserType userType) {
        return findByPhoneAndUserType(phone, userType)
                .switchIfEmpty(Mono.error(new NotFoundException("User account not found")));
    }

    @Override
    public Mono<UserAccount> findByPhoneAndUserType(NormalizedPhone phone, UserType userType) {
        return repository.findByCountryCodeAndPhoneNumberAndUserType(
                phone.countryCode(),
                phone.phoneNumber(),
                userType
        );
    }
}
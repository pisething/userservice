package com.pisethjavaschool.userservice.user.service.impl;

import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.domain.enumeration.AccountStatus;
import com.pisethjavaschool.userservice.user.domain.enumeration.RegistrationStatus;
import com.pisethjavaschool.userservice.user.repository.UserAccountRepository;
import com.pisethjavaschool.userservice.user.service.UserAccountStateService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserAccountStateServiceImpl implements UserAccountStateService {

    private final UserAccountRepository repository;
    private final Clock clock;

    @Override
    public Mono<UserAccount> markOtpVerified(UserAccount account) {
        return updateRegistrationStatus(account, RegistrationStatus.OTP_VERIFIED);
    }

    @Override
    public Mono<UserAccount> markProfileCompleted(UserAccount account) {
        return updateRegistrationStatus(account, RegistrationStatus.PROFILE_COMPLETED);
    }

    @Override
    public Mono<UserAccount> activate(UserAccount account) {
        account.setRegistrationStatus(RegistrationStatus.PIN_SET);
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setUpdatedAt(Instant.now(clock));

        return repository.save(account);
    }

    @Override
    public Mono<UserAccount> updateRegistrationStatus(UserAccount account, RegistrationStatus status) {
        account.setRegistrationStatus(status);
        account.setUpdatedAt(Instant.now(clock));

        return repository.save(account);
    }
}
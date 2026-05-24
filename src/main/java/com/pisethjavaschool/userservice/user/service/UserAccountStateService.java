package com.pisethjavaschool.userservice.user.service;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.domain.enumeration.RegistrationStatus;

import reactor.core.publisher.Mono;

public interface UserAccountStateService {

    Mono<UserAccount> markOtpVerified(UserAccount account);

    Mono<UserAccount> markProfileCompleted(UserAccount account);

    Mono<UserAccount> activate(UserAccount account);

    Mono<UserAccount> updateRegistrationStatus(UserAccount account, RegistrationStatus status);
}
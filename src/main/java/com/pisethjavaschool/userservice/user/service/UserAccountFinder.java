package com.pisethjavaschool.userservice.user.service;

import java.util.UUID;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;
import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;

import reactor.core.publisher.Mono;

public interface UserAccountFinder {

    Mono<UserAccount> findRequiredById(UUID userAccountId);

    Mono<UserAccount> findRequiredByPhoneAndUserType(NormalizedPhone phone, UserType userType);

    Mono<UserAccount> findByPhoneAndUserType(NormalizedPhone phone, UserType userType);
}
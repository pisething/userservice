package com.pisethjavaschool.userservice.user.service;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;
import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;

import reactor.core.publisher.Mono;

public interface UserRegistrationCreator {

    Mono<UserAccount> create(NormalizedPhone phone, UserType userType);
}
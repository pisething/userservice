package com.pisethjavaschool.userservice.user.service;

import com.pisethjavaschool.userservice.user.domain.CustomerProfile;
import com.pisethjavaschool.userservice.user.domain.UserAccount;

import reactor.core.publisher.Mono;

public interface UserIdentityProvisioningService {

    Mono<UserAccount> provisionOrResetPin(UserAccount account, CustomerProfile profile, String pin);
}
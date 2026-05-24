package com.pisethjavaschool.userservice.user.validation;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.domain.enumeration.AccountStatus;
import com.pisethjavaschool.userservice.user.domain.enumeration.RegistrationStatus;
import com.pisethjavaschool.userservice.user.exception.AccountInactiveException;
import com.pisethjavaschool.userservice.user.exception.RegistrationIncompleteException;

import reactor.core.publisher.Mono;

@Component
public class LoginValidator {

    public Mono<Void> validateCanLogin(UserAccount account) {
        if (account.getRegistrationStatus() != RegistrationStatus.PIN_SET) {
            return Mono.error(new RegistrationIncompleteException());
        }

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            return Mono.error(new AccountInactiveException());
        }

        return Mono.empty();
    }
}
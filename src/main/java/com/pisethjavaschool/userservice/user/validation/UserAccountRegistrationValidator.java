package com.pisethjavaschool.userservice.user.validation;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.domain.enumeration.RegistrationStatus;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;
import com.pisethjavaschool.userservice.user.exception.InvalidUserTypeException;
import com.pisethjavaschool.userservice.user.exception.OtpNotVerifiedException;
import com.pisethjavaschool.userservice.user.exception.ProfileNotCompletedException;
import com.pisethjavaschool.userservice.user.exception.UserAlreadyRegisteredException;

import reactor.core.publisher.Mono;

@Component
public class UserAccountRegistrationValidator {

    public Mono<Void> validateCanRegisterOrResume(UserAccount account) {
        if (account.getRegistrationStatus() == RegistrationStatus.PIN_SET) {
            return Mono.error(new UserAlreadyRegisteredException());
        }

        return Mono.empty();
    }

    public Mono<Void> validateCanCompleteCustomerProfile(UserAccount account) {
        if (account.getUserType() != UserType.CUSTOMER) {
            return Mono.error(new InvalidUserTypeException("This profile API is only for CUSTOMER"));
        }

        if (account.getRegistrationStatus() != RegistrationStatus.OTP_VERIFIED
                && account.getRegistrationStatus() != RegistrationStatus.PROFILE_COMPLETED) {
            return Mono.error(new OtpNotVerifiedException());
        }

        return Mono.empty();
    }

    public Mono<Void> validateCanSetPin(UserAccount account) {
        if (account.getRegistrationStatus() != RegistrationStatus.PROFILE_COMPLETED) {
            return Mono.error(new ProfileNotCompletedException());
        }

        return Mono.empty();
    }
}
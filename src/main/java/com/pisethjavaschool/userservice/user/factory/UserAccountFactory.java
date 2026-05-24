package com.pisethjavaschool.userservice.user.factory;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.domain.enumeration.AccountStatus;
import com.pisethjavaschool.userservice.user.domain.enumeration.RegistrationStatus;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserAccountFactory {

    private final Clock clock;

    public UserAccount create(String countryCode, String phoneNumber, UserType userType) {
        Instant now = Instant.now(clock);
        UserAccount account = new UserAccount();
        account.setUserType(userType);
        account.setCountryCode(countryCode);
        account.setPhoneNumber(phoneNumber);
        account.setRegistrationStatus(RegistrationStatus.PHONE_REGISTERED);
        account.setAccountStatus(AccountStatus.PENDING);
        account.setCreatedAt(now);
        account.setUpdatedAt(now);
        return account;
    }
}

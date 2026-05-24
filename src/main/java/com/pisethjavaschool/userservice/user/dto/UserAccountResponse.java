package com.pisethjavaschool.userservice.user.dto;

import java.util.UUID;

import com.pisethjavaschool.userservice.user.domain.enumeration.AccountStatus;
import com.pisethjavaschool.userservice.user.domain.enumeration.RegistrationStatus;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;

public record UserAccountResponse(
        UUID id,
        String keycloakUserId,
        UserType userType,
        String countryCode,
        String phoneNumber,
        RegistrationStatus registrationStatus,
        AccountStatus accountStatus
) {
}

package com.pisethjavaschool.userservice.user.dto;

import java.util.UUID;

import com.pisethjavaschool.userservice.user.domain.enumeration.AccountStatus;
import com.pisethjavaschool.userservice.user.domain.enumeration.RegistrationStatus;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;

public record RegistrationStatusResponse(
        Boolean exists,
        UUID userAccountId,
        UserType userType,
        RegistrationStatus registrationStatus,
        AccountStatus accountStatus,
        String nextStep
) {
}

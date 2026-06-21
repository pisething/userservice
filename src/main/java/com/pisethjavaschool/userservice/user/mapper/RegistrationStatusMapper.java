package com.pisethjavaschool.userservice.user.mapper;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.domain.enumeration.RegistrationStatus;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;
import com.pisethjavaschool.userservice.user.dto.RegistrationStatusResponse;

@Component
public class RegistrationStatusMapper {
	
	public RegistrationStatusResponse toResponse(UserAccount account) {
	        return toResponse(account, null);
	}

    public RegistrationStatusResponse toResponse(UserAccount account, String registrationToken) {
        return new RegistrationStatusResponse(
                true,
                account.getId(),
                account.getUserType(),
                account.getRegistrationStatus(),
                account.getAccountStatus(),
                resolveNextStep(account.getRegistrationStatus()),
                registrationToken
        );
    }

    public RegistrationStatusResponse notRegistered(UserType userType) {
        return new RegistrationStatusResponse(
                false,
                null,
                userType,
                null,
                null,
                "REGISTER_PHONE",
                null
        );
    }

    private String resolveNextStep(RegistrationStatus status) {
        return switch (status) {
            case PHONE_REGISTERED -> "VERIFY_OTP";
            case OTP_VERIFIED -> "COMPLETE_PROFILE";
            case PROFILE_COMPLETED -> "SET_PIN";
            case PIN_SET -> "LOGIN";
        };
    }
}
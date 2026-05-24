package com.pisethjavaschool.userservice.user.mapper;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.CustomerProfile;
import com.pisethjavaschool.userservice.user.dto.CustomerProfileResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomerProfileResponseMapper {

	public CustomerProfileResponse toResponse(CustomerProfile profile) {
        return new CustomerProfileResponse(
                profile.getId(),
                profile.getUserAccountId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getDateOfBirth(),
                profile.getGender(),
                profile.getEmail(),
                profile.getNid(),
                profile.getReferralCode(),
                profile.getPhotoObjectKey(),
                null
        );
    }
}
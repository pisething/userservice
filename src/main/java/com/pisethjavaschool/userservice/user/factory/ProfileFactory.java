package com.pisethjavaschool.userservice.user.factory;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.user.domain.CustomerProfile;
import com.pisethjavaschool.userservice.user.dto.CustomerProfileRequest;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProfileFactory {

    private final Clock clock;

    public CustomerProfile createCustomerProfile(UUID userAccountId, CustomerProfileRequest request) {
        Instant now = Instant.now(clock);
        CustomerProfile profile = new CustomerProfile();
        profile.setUserAccountId(userAccountId);
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setDateOfBirth(request.dateOfBirth());
        profile.setGender(request.gender());
        profile.setEmail(request.email());
        profile.setNid(request.nid());
        profile.setReferralCode(request.referralCode());
        profile.setPhotoObjectKey(request.photoObjectKey());
        profile.setCreatedAt(now);
        profile.setUpdatedAt(now);
        return profile;
    }

    public void updateCustomerProfile(CustomerProfile profile, CustomerProfileRequest request) {
    	profile.setFirstName(request.firstName());
    	profile.setLastName(request.lastName());
        profile.setDateOfBirth(request.dateOfBirth());
        profile.setGender(request.gender());
        profile.setEmail(request.email());
        profile.setNid(request.nid());
        profile.setReferralCode(request.referralCode());
        profile.setPhotoObjectKey(request.photoObjectKey());
        profile.setUpdatedAt(Instant.now(clock));
    }
}

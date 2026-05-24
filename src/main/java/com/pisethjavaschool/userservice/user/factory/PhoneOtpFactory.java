package com.pisethjavaschool.userservice.user.factory;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

import com.pisethjavaschool.userservice.config.OtpProperties;
import com.pisethjavaschool.userservice.user.domain.PhoneOtp;
import com.pisethjavaschool.userservice.user.domain.enumeration.OtpPurpose;
import com.pisethjavaschool.userservice.user.domain.enumeration.OtpStatus;
import com.pisethjavaschool.userservice.user.service.OtpHashingService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PhoneOtpFactory {

    private final OtpProperties properties;
    private final OtpCodeGenerator otpCodeGenerator;
    private final OtpHashingService otpHashingService;
    private final Clock clock;

    public PhoneOtp create(String countryCode, String phoneNumber, OtpPurpose purpose) {
        Instant now = Instant.now(clock);
        
        String rawOtp = otpCodeGenerator.generate();

        PhoneOtp otp = new PhoneOtp();
        otp.setCountryCode(countryCode);
        otp.setPhoneNumber(phoneNumber);
        otp.setPurpose(purpose);

        otp.setOtpCode(rawOtp); // for sender only
        otp.setOtpHash(otpHashingService.hash(rawOtp)); // saved to DB
        //otp.setOtpCode(otpCodeGenerator.generate());
        otp.setStatus(OtpStatus.PENDING);
        otp.setAttempts(0);
        otp.setExpiresAt(now.plus(properties.expireMinutes(), ChronoUnit.MINUTES));
        otp.setCreatedAt(now);
        otp.setUpdatedAt(now);

        return otp;
    }
}
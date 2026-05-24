package com.pisethjavaschool.userservice.user.domain;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;

import com.pisethjavaschool.userservice.user.domain.enumeration.OtpPurpose;
import com.pisethjavaschool.userservice.user.domain.enumeration.OtpStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("phone_otp")
public class PhoneOtp {

    @Id
    private UUID id;
    private String countryCode;
    private String phoneNumber;
    @Transient
    private String otpCode;
    private String otpHash;
    private OtpPurpose purpose;
    private OtpStatus status;
    private Integer attempts;
    private Instant expiresAt;
    private Instant verifiedAt;
    private Instant createdAt;
    private Instant updatedAt;
}

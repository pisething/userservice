package com.pisethjavaschool.userservice.user.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.pisethjavaschool.userservice.user.domain.PhoneOtp;
import com.pisethjavaschool.userservice.user.domain.enumeration.OtpPurpose;
import com.pisethjavaschool.userservice.user.domain.enumeration.OtpStatus;

import reactor.core.publisher.Mono;

public interface PhoneOtpRepository extends ReactiveCrudRepository<PhoneOtp, UUID> {

    Mono<PhoneOtp> findFirstByCountryCodeAndPhoneNumberAndPurposeAndStatusOrderByCreatedAtDesc(
            String countryCode,
            String phoneNumber,
            OtpPurpose purpose,
            OtpStatus status
    );

    @Modifying
    @Query("""
            UPDATE phone_otp
            SET status = 'EXPIRED'
            WHERE country_code = :countryCode
              AND phone_number = :phoneNumber
              AND purpose = :purpose
              AND status = 'PENDING'
            """)
    Mono<Integer> expirePendingOtps(
            String countryCode,
            String phoneNumber,
            String purpose
    );
}
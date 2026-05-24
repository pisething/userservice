package com.pisethjavaschool.userservice.user.service.impl;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pisethjavaschool.userservice.config.OtpProperties;
import com.pisethjavaschool.userservice.user.domain.PhoneOtp;
import com.pisethjavaschool.userservice.user.domain.enumeration.OtpPurpose;
import com.pisethjavaschool.userservice.user.domain.enumeration.OtpStatus;
import com.pisethjavaschool.userservice.user.exception.InvalidOtpException;
import com.pisethjavaschool.userservice.user.exception.OtpExpiredException;
import com.pisethjavaschool.userservice.user.exception.OtpMaxAttemptsReachedException;
import com.pisethjavaschool.userservice.user.exception.OtpNotFoundException;
import com.pisethjavaschool.userservice.user.exception.OtpResendTooSoonException;
import com.pisethjavaschool.userservice.user.factory.PhoneOtpFactory;
import com.pisethjavaschool.userservice.user.repository.PhoneOtpRepository;
import com.pisethjavaschool.userservice.user.service.OtpHashingService;
import com.pisethjavaschool.userservice.user.service.OtpSender;
import com.pisethjavaschool.userservice.user.service.OtpService;
import com.pisethjavaschool.userservice.user.util.LogMasker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final PhoneOtpRepository repository;
    private final PhoneOtpFactory phoneOtpFactory;
    private final OtpSender otpSender;
    private final OtpHashingService otpHashingService;
    private final OtpProperties properties;
    private final Clock clock;

    /*
    @Override
    @Transactional
    public Mono<Void> sendOtp(String countryCode, String phoneNumber, OtpPurpose purpose) {
        return findLatestPendingOtp(countryCode, phoneNumber, purpose)
                .flatMap(this::validateResendAllowed)
                .then(expirePendingOtps(countryCode, phoneNumber, purpose))
                .then(createAndSendOtp(countryCode, phoneNumber, purpose));
    }
    */
    
    @Override
    @Transactional
    public Mono<Void> sendOtp(String countryCode, String phoneNumber, OtpPurpose purpose) {
        log.info(
                "OTP send requested. purpose={}, phone={}",
                purpose,
                LogMasker.maskPhone(phoneNumber)
        );

        return findLatestPendingOtp(countryCode, phoneNumber, purpose)
                .flatMap(this::validateResendAllowed)
                .then(expirePendingOtps(countryCode, phoneNumber, purpose))
                .then(createAndSendOtp(countryCode, phoneNumber, purpose))
                .doOnSuccess(ignored -> log.info(
                        "OTP sent successfully. purpose={}, phone={}",
                        purpose,
                        LogMasker.maskPhone(phoneNumber)
                ))
                .doOnError(error -> log.warn(
                        "OTP send failed. purpose={}, phone={}, reason={}",
                        purpose,
                        LogMasker.maskPhone(phoneNumber),
                        error.getMessage()
                ));
    }

    /*
    @Override
    @Transactional
    public Mono<Void> verifyOtp(
            String countryCode,
            String phoneNumber,
            OtpPurpose purpose,
            String otpCode
    ) {
        return findLatestPendingOtp(countryCode, phoneNumber, purpose)
                .switchIfEmpty(Mono.error(new OtpNotFoundException()))
                .flatMap(otp -> verifyExistingOtp(otp, otpCode));
    }
    */
    
    @Override
    @Transactional
    public Mono<Void> verifyOtp(
            String countryCode,
            String phoneNumber,
            OtpPurpose purpose,
            String otpCode
    ) {
        log.info(
                "OTP verification requested. purpose={}, phone={}",
                purpose,
                LogMasker.maskPhone(phoneNumber)
        );

        return findLatestPendingOtp(countryCode, phoneNumber, purpose)
                .switchIfEmpty(Mono.error(new OtpNotFoundException()))
                .flatMap(otp -> verifyExistingOtp(otp, otpCode))
                .doOnSuccess(ignored -> log.info(
                        "OTP verified successfully. purpose={}, phone={}",
                        purpose,
                        LogMasker.maskPhone(phoneNumber)
                ))
                .doOnError(error -> log.warn(
                        "OTP verification failed. purpose={}, phone={}, reason={}",
                        purpose,
                        LogMasker.maskPhone(phoneNumber),
                        error.getMessage()
                ));
    }

    private Mono<PhoneOtp> findLatestPendingOtp(
            String countryCode,
            String phoneNumber,
            OtpPurpose purpose
    ) {
        return repository.findFirstByCountryCodeAndPhoneNumberAndPurposeAndStatusOrderByCreatedAtDesc(
                countryCode,
                phoneNumber,
                purpose,
                OtpStatus.PENDING
        );
    }

    private Mono<PhoneOtp> validateResendAllowed(PhoneOtp otp) {
        Instant now = Instant.now(clock);

        if (otp.getExpiresAt().isBefore(now)) {
            return Mono.just(otp);
        }

        Instant nextAllowedAt = otp.getCreatedAt().plus(
                properties.resendAfterSeconds(),
                ChronoUnit.SECONDS
        );

        if (nextAllowedAt.isAfter(now)) {
            return Mono.error(new OtpResendTooSoonException());
        }

        return Mono.just(otp);
    }

    private Mono<Void> expirePendingOtps(
            String countryCode,
            String phoneNumber,
            OtpPurpose purpose
    ) {
        return repository.expirePendingOtps(
                        countryCode,
                        phoneNumber,
                        purpose.name()
                )
                .then();
    }

    private Mono<Void> createAndSendOtp(
            String countryCode,
            String phoneNumber,
            OtpPurpose purpose
    ) {
        PhoneOtp otp = phoneOtpFactory.create(countryCode, phoneNumber, purpose);

        // Copy object for sender before removing raw OTP.
        PhoneOtp otpForSend = copyOtpForSend(otp);

        // Never save raw OTP into database.
        otp.setOtpCode(null);

        return repository.save(otp)
                .then(otpSender.send(otpForSend));
    }

    private PhoneOtp copyOtpForSend(PhoneOtp otp) {
        PhoneOtp copiedOtp = new PhoneOtp();

        copiedOtp.setCountryCode(otp.getCountryCode());
        copiedOtp.setPhoneNumber(otp.getPhoneNumber());
        copiedOtp.setOtpCode(otp.getOtpCode());
        copiedOtp.setPurpose(otp.getPurpose());

        return copiedOtp;
    }

    private Mono<Void> verifyExistingOtp(PhoneOtp otp, String otpCode) {
        Instant now = Instant.now(clock);

        if (isExpired(otp, now)) {
            return markExpired(otp, now)
                    .then(Mono.error(new OtpExpiredException()));
        }

        if (isMaxAttemptsReached(otp)) {
            return Mono.error(new OtpMaxAttemptsReachedException());
        }

        if (!isOtpMatched(otp, otpCode)) {
            return increaseAttempt(otp, now)
                    .then(Mono.error(new InvalidOtpException()));
        }

        return markVerified(otp, now).then();
    }

    private boolean isExpired(PhoneOtp otp, Instant now) {
        return otp.getExpiresAt().isBefore(now);
    }

    private boolean isMaxAttemptsReached(PhoneOtp otp) {
        return otp.getAttempts() >= properties.maxAttempts();
    }

    private boolean isOtpMatched(PhoneOtp otp, String rawOtp) {
        // Compare request OTP with hashed OTP from DB.
        return otpHashingService.matches(rawOtp, otp.getOtpHash());
    }

    private Mono<PhoneOtp> increaseAttempt(PhoneOtp otp, Instant now) {
        otp.setAttempts(otp.getAttempts() + 1);
        otp.setUpdatedAt(now);

        return repository.save(otp);
    }

    private Mono<PhoneOtp> markExpired(PhoneOtp otp, Instant now) {
        otp.setStatus(OtpStatus.EXPIRED);
        otp.setUpdatedAt(now);

        return repository.save(otp);
    }

    private Mono<PhoneOtp> markVerified(PhoneOtp otp, Instant now) {
        otp.setStatus(OtpStatus.VERIFIED);
        otp.setVerifiedAt(now);
        otp.setUpdatedAt(now);

        return repository.save(otp);
    }
}
package com.pisethjavaschool.userservice.user.service.impl;

import org.springframework.stereotype.Service;

import com.pisethjavaschool.userservice.user.domain.PhoneOtp;
import com.pisethjavaschool.userservice.user.service.OtpSender;
import com.pisethjavaschool.userservice.user.util.LogMasker;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class LogOtpSender implements OtpSender {

    @Override
    public Mono<Void> send(PhoneOtp otp) {
    	log.info(
    	        "DEV ONLY - OTP generated. phone={}, otp={}",
    	        LogMasker.maskPhone(otp.getPhoneNumber()),
    	        otp.getOtpCode()
    	);

        return Mono.empty();
    }
}
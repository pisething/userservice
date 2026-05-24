package com.pisethjavaschool.userservice.user.service;

public interface OtpHashingService {

	String hash(String rawOtp);

	boolean matches(String rawOtp, String otpHash);
}
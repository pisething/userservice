package com.pisethjavaschool.userservice.user.service.impl;

import org.springframework.stereotype.Service;

import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;
import com.pisethjavaschool.userservice.user.service.PhoneNumberService;
import com.pisethjavaschool.userservice.user.util.PhoneNormalizer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhoneNumberServiceImpl implements PhoneNumberService {

    private final PhoneNormalizer phoneNormalizer;

    @Override
    public NormalizedPhone normalize(String countryCode, String phoneNumber) {
        return new NormalizedPhone(
                phoneNormalizer.normalizeCountryCode(countryCode),
                phoneNormalizer.normalizePhoneNumber(phoneNumber)
        );
    }
}
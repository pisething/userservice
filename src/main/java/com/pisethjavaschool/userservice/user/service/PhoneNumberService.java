package com.pisethjavaschool.userservice.user.service;

import com.pisethjavaschool.userservice.user.dto.NormalizedPhone;

public interface PhoneNumberService {

    NormalizedPhone normalize(String countryCode, String phoneNumber);
}
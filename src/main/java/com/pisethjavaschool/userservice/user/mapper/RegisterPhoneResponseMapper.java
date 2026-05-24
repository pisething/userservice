package com.pisethjavaschool.userservice.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.dto.RegisterPhoneResponse;

@Mapper(componentModel = "spring")
public interface RegisterPhoneResponseMapper {

    @Mapping(target = "userAccountId", source = "account.id")
    @Mapping(target = "registrationStatus", source = "account.registrationStatus")
    @Mapping(target = "message", source = "message")
    RegisterPhoneResponse toResponse(UserAccount account, String message);
}
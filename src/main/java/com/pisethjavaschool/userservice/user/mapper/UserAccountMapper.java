package com.pisethjavaschool.userservice.user.mapper;

import org.mapstruct.Mapper;

import com.pisethjavaschool.userservice.user.domain.UserAccount;
import com.pisethjavaschool.userservice.user.dto.UserAccountResponse;

@Mapper(componentModel = "spring")
public interface UserAccountMapper {

    UserAccountResponse toResponse(UserAccount account);
}
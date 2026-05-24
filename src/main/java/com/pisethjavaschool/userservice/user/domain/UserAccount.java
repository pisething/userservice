package com.pisethjavaschool.userservice.user.domain;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.pisethjavaschool.userservice.user.domain.enumeration.AccountStatus;
import com.pisethjavaschool.userservice.user.domain.enumeration.RegistrationStatus;
import com.pisethjavaschool.userservice.user.domain.enumeration.UserType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("user_account")
public class UserAccount {

    @Id
    private UUID id;
    private String keycloakUserId;
    private UserType userType;
    private String countryCode;
    private String phoneNumber;
    private RegistrationStatus registrationStatus;
    private AccountStatus accountStatus;
    private Instant createdAt;
    private Instant updatedAt;
}

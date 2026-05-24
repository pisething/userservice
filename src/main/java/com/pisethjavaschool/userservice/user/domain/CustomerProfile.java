package com.pisethjavaschool.userservice.user.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.pisethjavaschool.userservice.user.domain.enumeration.Gender;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("customer_profile")
public class CustomerProfile {

    @Id
    private UUID id;
    private UUID userAccountId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String email;
    private String nid;
    private String referralCode;
    private String photoObjectKey;
    private Instant createdAt;
    private Instant updatedAt;
}

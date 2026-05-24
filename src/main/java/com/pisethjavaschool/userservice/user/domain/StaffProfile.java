package com.pisethjavaschool.userservice.user.domain;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("staff_profile")
public class StaffProfile {

    @Id
    private UUID id;
    private UUID userAccountId;
    private String firstName;
    private String lastName;
    private String email;
    private String employeeCode;
    private UUID departmentId;
    private String position;
    private String photoObjectKey;
    private Instant createdAt;
    private Instant updatedAt;
}

package com.pisethjavaschool.userservice.user.domain;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table("delivery_profile")
public class DeliveryProfile {

    @Id
    private UUID id;
    private UUID userAccountId;
    private String fullName;
    private String firstName;
    private String lastName;
    private String vehicleType;
    private String vehiclePlateNumber;
    private String driverLicenseNo;
    private String photoObjectKey;
    private Instant createdAt;
    private Instant updatedAt;
}

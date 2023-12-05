package com.fyp.dhumbal.user.dal;

import com.fyp.dhumbal.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UserEntity extends BaseEntity {

    @Column(name = "external_id", unique = true)
    private String externalId;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String otp;
    private Long otpExpiry;

    private boolean verified = false;

    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;

    private boolean online = false;

    private long lastOnline;
}

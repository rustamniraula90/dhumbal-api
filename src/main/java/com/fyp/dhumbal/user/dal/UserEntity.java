package com.fyp.dhumbal.user.dal;

import com.fyp.dhumbal.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    private boolean verified = false;

    @Column(name = "user_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType;
}

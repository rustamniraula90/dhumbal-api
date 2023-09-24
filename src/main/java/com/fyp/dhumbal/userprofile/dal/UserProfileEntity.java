package com.fyp.dhumbal.userprofile.dal;

import com.fyp.dhumbal.user.dal.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
public class UserProfileEntity {

    @Id
    @Column(name = "user_id")
    private String userId;

    @MapsId("user_id")
    @OneToOne
    private UserEntity user;

    private int gamesPlayed;
    private int gamesWon;
    private int gamesLost;
    private int totalPoints;

}

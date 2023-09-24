package com.fyp.dhumbal.user.rest.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserProfileResponse {
    private String userId;
    private int gamesPlayed;
    private int gamesWon;
    private int gamesLost;
    private int totalPoints;
}

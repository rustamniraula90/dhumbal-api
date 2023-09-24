package com.fyp.dhumbal.leaderboard.rest.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeaderBoardResponse {
    private Long rank;
    private String userId;
    private String name;
    private int score;
    private boolean isCurrentPlayer;
}

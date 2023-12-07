package com.fyp.dhumbal.game.rest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameUserResultResponse {
    private String userId;
    private String userName;
    private Integer points;
    private Boolean winner = false;
    private Integer score;
    private List<String> cards;
}

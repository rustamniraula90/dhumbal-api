package com.fyp.dhumbal.game.rest.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GameStateResponse {
    private String turn;
    private Boolean thrown = false;
    private Integer choiceCount = 0;
    private List<String> players = new ArrayList<>();
    private List<String> choices = new ArrayList<>();
    private List<String> hands = new ArrayList<>();
}

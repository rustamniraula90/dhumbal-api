package com.fyp.dhumbal.game.rest.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GameThrowRequest {
    private List<String> card;
}

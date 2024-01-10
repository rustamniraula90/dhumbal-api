package com.fyp.dhumbal.game.rest.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RunningGameResponse {
    private String id;
    private String code;
    private String owner;
    private Integer playerCount;
}

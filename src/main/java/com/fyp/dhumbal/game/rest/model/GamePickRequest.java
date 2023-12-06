package com.fyp.dhumbal.game.rest.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GamePickRequest {
    private boolean floor;
    private int choice = 0;
}

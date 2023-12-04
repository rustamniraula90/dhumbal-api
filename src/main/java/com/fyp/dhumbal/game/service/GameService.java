package com.fyp.dhumbal.game.service;

import com.fyp.dhumbal.game.rest.model.GamePickRequest;
import com.fyp.dhumbal.game.rest.model.GameStateResponse;
import com.fyp.dhumbal.game.rest.model.GameThrowRequest;

public interface GameService {

    void startGame(String id);

    void pickCard(String id, GamePickRequest request);

    void throwCard(String id, GameThrowRequest request);

    void endGame(String id);

    void dhumbal(String id);

    void passGame(String id);

    void finalizeGame(String id);

    GameStateResponse getGameState(String gameId);
}

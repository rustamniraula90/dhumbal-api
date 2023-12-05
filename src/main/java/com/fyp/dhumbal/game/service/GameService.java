package com.fyp.dhumbal.game.service;

import com.fyp.dhumbal.game.rest.model.GamePickRequest;
import com.fyp.dhumbal.game.rest.model.GameStateResponse;
import com.fyp.dhumbal.game.rest.model.GameThrowRequest;
import com.fyp.dhumbal.game.rest.model.GameUserResultResponse;

import java.util.List;

public interface GameService {

    void startGame(String id);

    void pickCard(String id, GamePickRequest request);

    void throwCard(String id, GameThrowRequest request);

    void endGame(String id);

    void finalizeGame(String id);

    GameStateResponse getGameState(String gameId);

    List<GameUserResultResponse> getResult(String gameId);
}

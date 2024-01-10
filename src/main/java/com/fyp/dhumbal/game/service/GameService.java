package com.fyp.dhumbal.game.service;

import com.fyp.dhumbal.game.rest.model.*;

import java.util.List;

public interface GameService {

    void startGame(String id);

    void pickCard(String id, GamePickRequest request, String userId, String username);

    void throwCard(String id, GameThrowRequest request, String userId, String username);

    void endGame(String id, String userId);

    void finalizeGame(String id);

    GameStateResponse getGameState(String gameId);

    List<GameUserResultResponse> getResult(String gameId);

    List<RunningGameResponse> getRunningGames();
}

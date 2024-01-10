package com.fyp.dhumbal.game.rest.controller;

import com.fyp.dhumbal.game.rest.model.*;
import com.fyp.dhumbal.game.service.GameService;
import com.fyp.dhumbal.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/game")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping("/{id}/pick")
    public void pickCard(@PathVariable("id") String gameId, @RequestBody GamePickRequest request) {
        gameService.pickCard(gameId, request, AuthUtil.getLoggedInUserId(), AuthUtil.getLoggedInUserName());
    }

    @PostMapping("/{id}/throw")
    public void throwCard(@PathVariable("id") String gameId, @RequestBody GameThrowRequest request) {
        gameService.throwCard(gameId, request, AuthUtil.getLoggedInUserId(), AuthUtil.getLoggedInUserName());
    }

    @PostMapping("/{id}/end")
    public void endGame(@PathVariable("id") String gameId) {
        gameService.endGame(gameId, AuthUtil.getLoggedInUserId());
    }

    @GetMapping("/{id}/result")
    public List<GameUserResultResponse> getGameResult(@PathVariable("id") String gameId) {
        return gameService.getResult(gameId);
    }

    @GetMapping("/{id}/state")
    public GameStateResponse getGameState(@PathVariable("id") String gameId) {
        return gameService.getGameState(gameId);
    }

    @GetMapping
    public List<RunningGameResponse> getRunningGames() {
        return gameService.getRunningGames();
    }

}

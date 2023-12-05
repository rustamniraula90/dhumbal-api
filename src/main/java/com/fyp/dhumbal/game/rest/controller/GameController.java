package com.fyp.dhumbal.game.rest.controller;

import com.fyp.dhumbal.game.rest.model.GamePickRequest;
import com.fyp.dhumbal.game.rest.model.GameStateResponse;
import com.fyp.dhumbal.game.rest.model.GameThrowRequest;
import com.fyp.dhumbal.game.rest.model.GameUserResultResponse;
import com.fyp.dhumbal.game.service.GameService;
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
        gameService.pickCard(gameId, request);
    }

    @PostMapping("/{id}/throw")
    public void throwCard(@PathVariable("id") String gameId, @RequestBody GameThrowRequest request) {
        gameService.throwCard(gameId, request);
    }

    @PostMapping("/{id}/end")
    public void endGame(@PathVariable("id") String gameId) {
        gameService.endGame(gameId);
    }

    @GetMapping("/{id}/result")
    public List<GameUserResultResponse> getGameResult(@PathVariable("id") String gameId) {
        return gameService.getResult(gameId);
    }

    @GetMapping("/{id}/state")
    public GameStateResponse getGameState(@PathVariable("id") String gameId) {
        return gameService.getGameState(gameId);
    }

}

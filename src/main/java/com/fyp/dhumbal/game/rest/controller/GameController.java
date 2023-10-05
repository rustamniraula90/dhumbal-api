package com.fyp.dhumbal.game.rest.controller;

import com.fyp.dhumbal.game.rest.model.GamePickRequest;
import com.fyp.dhumbal.game.rest.model.GameThrowRequest;
import com.fyp.dhumbal.game.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{id}/pass")
    public void passGame(@PathVariable("id") String gameId) {
        gameService.passGame(gameId);
    }

    @PostMapping("/{id}/dhumbal")
    public void dhumbal(@PathVariable("id") String gameId) {
        gameService.dhumbal(gameId);
    }

}

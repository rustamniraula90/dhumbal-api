package com.fyp.dhumbal.leaderboard.rest.controller;

import com.fyp.dhumbal.leaderboard.rest.model.LeaderBoardResponse;
import com.fyp.dhumbal.leaderboard.service.LeaderBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/leaderboard")
@RequiredArgsConstructor
public class LeaderBoardController {

    private final LeaderBoardService leaderBoardService;

    @GetMapping
    public List<LeaderBoardResponse> getLeaderBoard(@RequestParam(name = "size", required = false, defaultValue = "10") int size) {
        if (size > 100) size = 100;
        return leaderBoardService.getLeaderBoard(size);
    }
}

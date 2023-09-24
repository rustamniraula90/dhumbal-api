package com.fyp.dhumbal.leaderboard.service;

import com.fyp.dhumbal.leaderboard.rest.model.LeaderBoardResponse;

import java.util.List;

public interface LeaderBoardService {
    List<LeaderBoardResponse> getLeaderBoard(int size);
}

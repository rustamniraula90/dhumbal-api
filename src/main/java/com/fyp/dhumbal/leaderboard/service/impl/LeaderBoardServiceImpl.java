package com.fyp.dhumbal.leaderboard.service.impl;

import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.BadRequestException;
import com.fyp.dhumbal.global.util.AuthUtil;
import com.fyp.dhumbal.leaderboard.mapper.LeaderBoardMapper;
import com.fyp.dhumbal.leaderboard.rest.model.LeaderBoardResponse;
import com.fyp.dhumbal.leaderboard.service.LeaderBoardService;
import com.fyp.dhumbal.userprofile.dal.UserProfileEntity;
import com.fyp.dhumbal.userprofile.dal.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LeaderBoardServiceImpl implements LeaderBoardService {

    private final UserProfileRepository userProfileRepository;
    private final LeaderBoardMapper leaderBoardMapper;

    @Override
    public List<LeaderBoardResponse> getLeaderBoard(int size) {
        String currentUserId = Objects.requireNonNull(AuthUtil.getLoggedInUserId());
        boolean hasCurrentUser = false;
        List<UserProfileEntity> topPlayer = userProfileRepository.findTopPlayer(Pageable.ofSize(size));
        List<LeaderBoardResponse> response = leaderBoardMapper.toLeaderBoardResponse(topPlayer);
        for (int i = 1; i <= response.size(); i++) {
            response.get(i - 1).setRank((long) i);
            if (response.get(i - 1).getUserId().equals(currentUserId)) {
                response.get(i - 1).setCurrentPlayer(true);
                hasCurrentUser = true;
            }
        }
        if (!hasCurrentUser) {
            UserProfileEntity currentUser = userProfileRepository.findById(currentUserId).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "User not found"));
            if (currentUser != null) {
                LeaderBoardResponse currentUserResponse = leaderBoardMapper.toLeaderBoardResponse(currentUser);
                currentUserResponse.setRank(userProfileRepository.getUserRankByTotalPoints(currentUser.getTotalPoints()));
                currentUserResponse.setScore(currentUser.getTotalPoints());
                currentUserResponse.setCurrentPlayer(true);
                response.add(currentUserResponse);
            }
        }
        return response;
    }
}

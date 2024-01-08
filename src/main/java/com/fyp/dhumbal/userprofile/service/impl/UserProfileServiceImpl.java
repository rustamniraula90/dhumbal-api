package com.fyp.dhumbal.userprofile.service.impl;

import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.BadRequestException;
import com.fyp.dhumbal.user.dal.UserEntity;
import com.fyp.dhumbal.userprofile.dal.UserProfileEntity;
import com.fyp.dhumbal.userprofile.dal.UserProfileRepository;
import com.fyp.dhumbal.userprofile.mapper.UserProfileMapper;
import com.fyp.dhumbal.user.rest.model.GetUserProfileResponse;
import com.fyp.dhumbal.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    @Value("${dhumbal.game.points.multiplier}")
    private Integer gamePointMultiplier;

    @Override
    public GetUserProfileResponse getUserProfileById(String userId) {
        return userProfileMapper.toResponse(userProfileRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "User not found")));
    }

    @Override
    public void createUserProfile(UserEntity userEntity) {
        UserProfileEntity userProfile = new UserProfileEntity();
        userProfile.setUserId(userEntity.getId());
        userProfileRepository.save(userProfile);
    }

    @Override
    public void updateStatus(String userId, int gamePoints) {
        UserProfileEntity userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "User not found"));
        boolean winner = gamePoints > 0;
        userProfile.setGamesPlayed(userProfile.getGamesPlayed() + 1);
        userProfile.setTotalPoints(userProfile.getTotalPoints() + (gamePoints * gamePointMultiplier));
        if (winner) {
            userProfile.setGamesWon(userProfile.getGamesWon() + 1);
        } else {
            userProfile.setGamesLost(userProfile.getGamesLost() + 1);
        }
        userProfileRepository.save(userProfile);
    }
}

package com.fyp.dhumbal.leaderboard.mapper;

import com.fyp.dhumbal.leaderboard.rest.model.LeaderBoardResponse;
import com.fyp.dhumbal.userprofile.dal.UserProfileEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LeaderBoardMapper {

    List<LeaderBoardResponse> toLeaderBoardResponse(List<UserProfileEntity> players);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "score", source = "totalPoints")
    LeaderBoardResponse toLeaderBoardResponse(UserProfileEntity players);

}

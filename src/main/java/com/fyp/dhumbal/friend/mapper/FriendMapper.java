package com.fyp.dhumbal.friend.mapper;

import com.fyp.dhumbal.friend.dal.FriendEntity;
import com.fyp.dhumbal.friend.dal.FriendshipStatus;
import com.fyp.dhumbal.friend.rest.model.FriendResponse;
import com.fyp.dhumbal.user.dal.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FriendMapper {

    @Mapping(target = "user.email", ignore = true)
    FriendResponse toResponse(UserEntity user, FriendshipStatus status);

    default FriendResponse toResponse(FriendEntity entity, String currentUserId) {
        if (entity.getUser1().getId().equals(currentUserId))
            return toResponse(entity.getUser2(), entity.getStatus());
        else return toResponse(entity.getUser1(), entity.getStatus());

    }

    default List<FriendResponse> toResponse(List<FriendEntity> friends, String currentUserId) {
        return friends.stream().map(f -> toResponse(f, currentUserId)).toList();
    }
}

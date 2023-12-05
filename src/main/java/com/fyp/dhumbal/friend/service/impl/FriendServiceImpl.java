package com.fyp.dhumbal.friend.service.impl;

import com.fyp.dhumbal.friend.dal.FriendEntity;
import com.fyp.dhumbal.friend.dal.FriendRepository;
import com.fyp.dhumbal.friend.dal.FriendshipStatus;
import com.fyp.dhumbal.friend.mapper.FriendMapper;
import com.fyp.dhumbal.friend.rest.model.FriendResponse;
import com.fyp.dhumbal.friend.rest.model.FriendRequest;
import com.fyp.dhumbal.friend.service.FriendService;
import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.BadRequestException;
import com.fyp.dhumbal.global.util.AuthUtil;
import com.fyp.dhumbal.user.dal.UserEntity;
import com.fyp.dhumbal.user.dal.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final FriendMapper friendMapper;
    private final UserRepository userRepository;

    @Override
    public FriendResponse sendFriendRequest(FriendRequest request) {
        String currentUserId = AuthUtil.getLoggedInUserId();
        Optional<FriendEntity> existing = friendRepository.findFriendship(request.getUserId(), currentUserId);
        if (existing.isPresent()) {
            String message = "User is already friend";
            if (existing.get().getStatus() == FriendshipStatus.REQUESTED) {
                message = "There is already a friend request with user";
            }
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, message);

        }
        UserEntity own = userRepository.findById(currentUserId).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "User not found!!"));
        UserEntity user = userRepository.findById(request.getUserId()).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "User not found!!"));
        return friendMapper.toResponse(friendRepository.save(new FriendEntity(own, user, FriendshipStatus.REQUESTED)), currentUserId);
    }

    @Override
    public List<FriendResponse> getFriendRequest() {
        String currentUserId = AuthUtil.getLoggedInUserId();
        return friendMapper.toResponse(friendRepository.findByUser2_IdAndStatus(currentUserId, FriendshipStatus.REQUESTED), currentUserId);
    }

    @Override
    public List<FriendResponse> getFriends() {
        String currentUserId = AuthUtil.getLoggedInUserId();
        return friendMapper.toResponse(friendRepository.findFriendship(currentUserId, FriendshipStatus.ACCEPTED), currentUserId);
    }

    @Override
    public void removeFriend(FriendRequest request) {
        String currentUserId = AuthUtil.getLoggedInUserId();
        friendRepository.findFriendship(request.getUserId(), currentUserId).ifPresent(friendRepository::delete);
    }

    @Override
    public void acceptFriendRequest(FriendRequest request) {
        String currentUserId = AuthUtil.getLoggedInUserId();
        friendRepository.findFriendship(request.getUserId(), currentUserId).ifPresent(friend -> {
            friend.setStatus(FriendshipStatus.ACCEPTED);
            friendRepository.save(friend);
        });
    }

    @Override
    public List<FriendResponse> getFriendsByOnline(boolean online) {
        String currentUserId = AuthUtil.getLoggedInUserId();
        return friendMapper.toResponse(friendRepository.findFriendship(currentUserId, FriendshipStatus.ACCEPTED, online), currentUserId);
    }
}

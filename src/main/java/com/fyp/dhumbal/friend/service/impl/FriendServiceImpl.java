package com.fyp.dhumbal.friend.service.impl;

import com.fyp.dhumbal.friend.dal.FriendEntity;
import com.fyp.dhumbal.friend.dal.FriendRepository;
import com.fyp.dhumbal.friend.dal.FriendshipStatus;
import com.fyp.dhumbal.friend.mapper.FriendMapper;
import com.fyp.dhumbal.friend.rest.model.FriendResponse;
import com.fyp.dhumbal.friend.rest.model.FriendRequest;
import com.fyp.dhumbal.friend.rest.model.InviteFriendRequest;
import com.fyp.dhumbal.friend.service.FriendService;
import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.BadRequestException;
import com.fyp.dhumbal.global.util.AuthUtil;
import com.fyp.dhumbal.room.dal.RoomEntity;
import com.fyp.dhumbal.room.dal.RoomRepository;
import com.fyp.dhumbal.updater.model.UpdateType;
import com.fyp.dhumbal.updater.service.UpdaterService;
import com.fyp.dhumbal.user.dal.UserEntity;
import com.fyp.dhumbal.user.dal.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;
    private final FriendMapper friendMapper;
    private final UserRepository userRepository;
    private final UpdaterService updaterService;
    private final RoomRepository roomRepository;

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

    @Override
    public void inviteFriendToRoom(InviteFriendRequest request) {
        String currentUserId = AuthUtil.getLoggedInUserId();
        friendRepository.findFriendship(request.getFriendId(), currentUserId).ifPresent(friend -> {
            if (friend.getStatus() == FriendshipStatus.ACCEPTED) {
                RoomEntity room = roomRepository.findById(request.getRoomId()).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
                Map<String, String> data = new HashMap<>();
                data.put("roomId", room.getId());
                data.put("roomCode", room.getCode());
                data.put("name", AuthUtil.getLoggedInUserName());
                updaterService.updateUser(request.getFriendId(), UpdateType.ROOM_INVITE, data);
            }
        });
    }
}
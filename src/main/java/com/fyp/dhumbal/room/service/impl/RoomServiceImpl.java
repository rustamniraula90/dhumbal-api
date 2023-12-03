package com.fyp.dhumbal.room.service.impl;

import com.fyp.dhumbal.game.service.GameService;
import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.BadRequestException;
import com.fyp.dhumbal.global.util.AuthUtil;
import com.fyp.dhumbal.global.util.RandomGenerator;
import com.fyp.dhumbal.room.dal.RoomEntity;
import com.fyp.dhumbal.room.dal.RoomRepository;
import com.fyp.dhumbal.room.dal.RoomStatusEnum;
import com.fyp.dhumbal.room.dal.member.RoomMemberEntity;
import com.fyp.dhumbal.room.dal.member.RoomMemberRepository;
import com.fyp.dhumbal.room.mapper.RoomMapper;
import com.fyp.dhumbal.room.rest.model.CreateRoomRequest;
import com.fyp.dhumbal.room.rest.model.RoomResponse;
import com.fyp.dhumbal.room.service.RoomService;
import com.fyp.dhumbal.updater.model.UpdateType;
import com.fyp.dhumbal.updater.service.UpdaterService;
import com.fyp.dhumbal.user.dal.UserEntity;
import com.fyp.dhumbal.user.dal.UserRepository;
import com.fyp.dhumbal.user.rest.model.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final RoomMapper roomMapper;
    private final UserRepository userRepository;
    private final GameService gameService;
    private final UpdaterService updaterService;

    @Value("${dhumbal.room.code.length}")
    private int roomCodeLength;
    @Value("${dhumbal.room.member.max}")
    private int roomMemberMax;
    @Value("${dhumbal.room.join.waitingSeconds}")
    private int roomJoinWaitingSeconds;

    @Override
    public RoomResponse createRoom(CreateRoomRequest request) {
        String code;
        do {
            code = RandomGenerator.generateAlphabetic(roomCodeLength);
        } while (roomRepository.existsByCode(code));
        UserEntity user = userRepository.findById(Objects.requireNonNull(AuthUtil.getLoggedInUserId())).orElseThrow(() -> new BadRequestException(ErrorCodes.INTERNAL_SERVER_ERROR, "User not found"));
        RoomEntity room = roomRepository.save(roomMapper.toEntity(code, user));
        RoomMemberEntity roomMemberEntity = roomMapper.toRoomMember(room);
        roomMemberEntity.setMembers(Collections.singletonList(user.getId()));
        roomMemberRepository.save(roomMemberEntity);
        return roomMapper.toResponse(room);
    }

    @Override
    public RoomResponse joinRoom(String code) {
        RoomEntity roomEntity = roomRepository.findByCodeAndStatus(code, RoomStatusEnum.WAITING).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        RoomMemberEntity roomMemberEntity = roomMemberRepository.findById(roomEntity.getId()).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        return joinRoom(roomEntity, roomMemberEntity);
    }

    public RoomResponse joinRoom(RoomEntity room, RoomMemberEntity roomMember) {
        if (roomMember.getMembers().size() <= roomMemberMax) {
            roomMember.getMembers().add(AuthUtil.getLoggedInUserId());
            roomMemberRepository.save(roomMember);
            updaterService.updateRoom(room.getId(), UpdateType.PLAYER_JOINED, AuthUtil.getLoggedInUserId());
        } else
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Room is full");
        return roomMapper.toResponse(room);
    }

    @Override
    public RoomResponse joinRandomRoom() {
        long waitingTime = System.currentTimeMillis() + (roomJoinWaitingSeconds * 1000L);
        while (waitingTime > System.currentTimeMillis()) {
            List<RoomEntity> waitingRooms = roomRepository.findByStatus(RoomStatusEnum.WAITING);
            if (!waitingRooms.isEmpty()) {
                RoomMemberEntity roomMemberEntity = roomMemberRepository.findById(waitingRooms.get(0).getId()).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
                return joinRoom(waitingRooms.get(0), roomMemberEntity);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return createRoom(new CreateRoomRequest(false));
    }

    @Override
    public RoomResponse leaveRoom(String code) {
        RoomEntity roomEntity = roomRepository.findByCodeAndStatus(code, RoomStatusEnum.WAITING).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        RoomMemberEntity roomMemberEntity = roomMemberRepository.findById(roomEntity.getId()).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        String userId = AuthUtil.getLoggedInUserId();
        if (roomMemberEntity.getOwnerId().equals(userId)) {
            roomMemberRepository.delete(roomMemberEntity);
            roomRepository.delete(roomEntity);
            updaterService.updateRoom(roomEntity.getId(), UpdateType.ROOM_ENDED, null);
        } else {
            roomMemberEntity.getMembers().remove(AuthUtil.getLoggedInUserId());
            roomMemberRepository.save(roomMemberEntity);
            updaterService.updateRoom(roomEntity.getId(), UpdateType.PLAYER_LEFT, AuthUtil.getLoggedInUserId());
        }
        return roomMapper.toResponse(roomEntity);
    }

    @Override
    public RoomResponse startRoom(String roomId) {
        RoomEntity roomEntity = roomRepository.findById(roomId).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        RoomMemberEntity roomMemberEntity = roomMemberRepository.findById(roomEntity.getId()).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        String userId = AuthUtil.getLoggedInUserId();
        if (!roomMemberEntity.getOwnerId().equals(userId)) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "You are not owner of this room");
        } else if (roomMemberEntity.getMembers().size() < 2) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Room must have at least 2 members");
        } else if (roomEntity.getStatus() != RoomStatusEnum.WAITING) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Room is already started");

        }
        roomEntity.setStatus(RoomStatusEnum.STARTED);
        roomRepository.save(roomEntity);
        gameService.startGame(roomId);
        return roomMapper.toResponse(roomEntity);
    }

    @Override
    public RoomResponse getRoomById(String roomId) {
        RoomEntity roomEntity = roomRepository.findById(roomId).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        RoomMemberEntity roomMemberEntity = roomMemberRepository.findById(roomEntity.getId()).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        RoomResponse response = roomMapper.toResponse(roomEntity);
        response.setMembers(new ArrayList<>());
        if (roomMemberEntity.getMembers() != null) {
            for (String member : roomMemberEntity.getMembers()) {
                UserEntity user = userRepository.findById(member).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "user not found"));
                response.getMembers().add(new UserResponse(user.getId(), user.getName(), ""));
            }
        }
        return response;
    }
}

package com.fyp.dhumbal.room.service.impl;

import com.fyp.dhumbal.game.service.GameService;
import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.BadRequestException;
import com.fyp.dhumbal.global.util.AuthUtil;
import com.fyp.dhumbal.global.util.RandomGenerator;
import com.fyp.dhumbal.room.dal.RoomEntity;
import com.fyp.dhumbal.room.dal.RoomRepository;
import com.fyp.dhumbal.room.dal.RoomStatusEnum;
import com.fyp.dhumbal.room.mapper.RoomMapper;
import com.fyp.dhumbal.room.rest.model.CreateRoomRequest;
import com.fyp.dhumbal.room.rest.model.RoomResponse;
import com.fyp.dhumbal.room.service.RoomService;
import com.fyp.dhumbal.updater.model.UpdateType;
import com.fyp.dhumbal.updater.service.UpdaterService;
import com.fyp.dhumbal.user.dal.UserEntity;
import com.fyp.dhumbal.user.dal.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
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
    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {
        String code;
        do {
            code = RandomGenerator.generateAlphabetic(roomCodeLength);
        } while (roomRepository.existsByCode(code));
        roomRepository.findByOwner_Id(AuthUtil.getLoggedInUserId()).ifPresent(room -> leaveRoom(room.getCode()));
        roomRepository.findByMembers_Id(AuthUtil.getLoggedInUserId()).ifPresent(room -> leaveRoom(room.getCode()));
        UserEntity user = userRepository.findById(Objects.requireNonNull(AuthUtil.getLoggedInUserId())).orElseThrow(() -> new BadRequestException(ErrorCodes.INTERNAL_SERVER_ERROR, "User not found"));
        RoomEntity room = roomMapper.toEntity(code, user, request.isPrivateRoom());
        room.setMembers(new ArrayList<>());
        room.getMembers().add(userRepository.findById(AuthUtil.getLoggedInUserId()).orElseThrow(() -> new BadRequestException(ErrorCodes.INTERNAL_SERVER_ERROR, "User not found")));
        return roomMapper.toResponse(roomRepository.save(room));
    }

    @Override
    @Transactional
    public RoomResponse joinRoom(String code) {
        RoomEntity roomEntity = roomRepository.findByCodeAndStatus(code, RoomStatusEnum.WAITING).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        return joinRoom(roomEntity);
    }

    public RoomResponse joinRoom(RoomEntity room) {
        if (room.getMembers().size() <= roomMemberMax) {
            room.getMembers().add(userRepository.findById(AuthUtil.getLoggedInUserId()).orElseThrow(() -> new BadRequestException(ErrorCodes.INTERNAL_SERVER_ERROR, "User not found")));
            roomRepository.save(room);
            Map<String, String> detail = new HashMap<>();
            detail.put("name", AuthUtil.getLoggedInUserName());
            detail.put("id", AuthUtil.getLoggedInUserId());
            updaterService.updateRoom(room.getId(), UpdateType.PLAYER_JOINED, detail);
        } else
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Room is full");
        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional
    public RoomResponse joinRandomRoom() {
        long waitingTime = System.currentTimeMillis() + (roomJoinWaitingSeconds * 1000L);
        while (waitingTime > System.currentTimeMillis()) {
            List<RoomEntity> waitingRooms = roomRepository.findByStatusAndPrivateRoom(RoomStatusEnum.WAITING, false);
            if (!waitingRooms.isEmpty()) {
                return joinRoom(waitingRooms.get(0));
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
    @Transactional
    public void leaveRoom(String code) {
        RoomEntity roomEntity = roomRepository.findByCode(code).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        String userId = AuthUtil.getLoggedInUserId();
        if (roomEntity.getMembers().size() < 2) {
            roomRepository.delete(roomEntity);
            updaterService.updateRoom(roomEntity.getId(), UpdateType.ROOM_ENDED, null);
            return;

        }
        roomEntity.getMembers().removeIf(user -> user.getId().equals(userId));
        if (roomEntity.getOwner().getId().equals(userId)) {
            roomEntity.setOwner(roomEntity.getMembers().get(0));
        }
        roomRepository.save(roomEntity);
        Map<String, String> detail = new HashMap<>();
        detail.put("name", AuthUtil.getLoggedInUserName());
        detail.put("id", AuthUtil.getLoggedInUserId());
        updaterService.updateRoom(roomEntity.getId(), UpdateType.PLAYER_LEFT, detail);
    }

    @Override
    @Transactional
    public RoomResponse startRoom(String roomId) {
        RoomEntity roomEntity = roomRepository.findById(roomId).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        String userId = AuthUtil.getLoggedInUserId();
        if (!roomEntity.getOwner().getId().equals(userId)) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "You are not owner of this room");
        } else if (roomEntity.getMembers().size() < 2) {
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
    @Transactional
    public RoomResponse getRoomById(String roomId) {
        RoomEntity roomEntity = roomRepository.findById(roomId).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        return roomMapper.toResponse(roomEntity);
    }
}

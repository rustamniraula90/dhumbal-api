package com.fyp.dhumbal.room.service.impl;

import com.fyp.dhumbal.agent.AgentConstant;
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
import com.fyp.dhumbal.user.rest.model.UserResponse;
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

    @Override
    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {
        String code;
        do {
            code = RandomGenerator.generateAlphabetic(roomCodeLength);
        } while (roomRepository.existsByCode(code));
        roomRepository.findByOwner_Id(AuthUtil.getLoggedInUserId()).ifPresent(room -> leaveRoom(room.getId()));
        roomRepository.findByMembers_Id(AuthUtil.getLoggedInUserId()).ifPresent(room -> leaveRoom(room.getId()));
        UserEntity user = userRepository.findById(Objects.requireNonNull(AuthUtil.getLoggedInUserId())).orElseThrow(() -> new BadRequestException(ErrorCodes.INTERNAL_SERVER_ERROR, "User not found"));
        RoomEntity room = roomMapper.toEntity(code, user, request.isPrivateRoom());
        room.setMembers(new ArrayList<>());
        room.getMembers().add(userRepository.findById(AuthUtil.getLoggedInUserId()).orElseThrow(() -> new BadRequestException(ErrorCodes.INTERNAL_SERVER_ERROR, "User not found")));
        return roomMapper.toResponse(roomRepository.save(room));
    }

    @Override
    @Transactional
    public RoomResponse joinRoom(String code) {
        String userId = AuthUtil.getLoggedInUserId();
        roomRepository.findByOwner_Id(userId).ifPresent(r -> leaveRoom(r.getId()));
        roomRepository.findByMembers_Id(userId).ifPresent(r -> leaveRoom(r.getId()));
        RoomEntity roomEntity = roomRepository.findByCodeAndStatus(code, RoomStatusEnum.WAITING).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        return joinRoom(roomEntity);
    }

    public RoomResponse joinRoom(RoomEntity room) {
        if (room.getMembers().size() + room.getAgent().size() >= roomMemberMax) {
            throw new BadRequestException(ErrorCodes.ROOM_FULL, "Room is full");
        }
        room.getMembers().add(userRepository.findById(AuthUtil.getLoggedInUserId()).orElseThrow(() -> new BadRequestException(ErrorCodes.INTERNAL_SERVER_ERROR, "User not found")));
        roomRepository.save(room);
        Map<String, String> detail = new HashMap<>();
        detail.put("name", AuthUtil.getLoggedInUserName());
        detail.put("id", AuthUtil.getLoggedInUserId());
        updaterService.updateRoom(room.getId(), UpdateType.PLAYER_JOINED, detail);
        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional
    public RoomResponse joinRandomRoom() {
        String userId = AuthUtil.getLoggedInUserId();
        roomRepository.findByOwner_Id(userId).ifPresent(r -> leaveRoom(r.getId()));
        roomRepository.findByMembers_Id(userId).ifPresent(r -> leaveRoom(r.getId()));
        List<RoomEntity> waitingRooms = roomRepository.findByStatusAndPrivateRoom(RoomStatusEnum.WAITING, false);
        if (!waitingRooms.isEmpty()) {
            for (RoomEntity room : waitingRooms) {
                if (room.getMembers().size() + room.getAgent().size() < roomMemberMax) {
                    return joinRoom(room);
                }
            }
        }
        return createRoom(new CreateRoomRequest(false));
    }

    @Override
    @Transactional
    public void leaveRoom(String id) {
        RoomEntity roomEntity = roomRepository.findById(id).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
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
        } else if (roomEntity.getMembers().size() + roomEntity.getAgent().size() < 2) {
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
        RoomResponse response = roomMapper.toResponse(roomEntity);
        int agentIndex = 0;
        for (Integer agent : roomEntity.getAgent()) {
            UserResponse userResponse = new UserResponse();
            userResponse.setId("BOT_" + agentIndex + AgentConstant.AGENT_ID_SEPARATOR + agent);
            userResponse.setName("Level " + agent + " Agent");
            response.getMembers().add(userResponse);
            agentIndex++;
        }
        // Rotate till current user is on 0 index
        int ownIndex = 0;
        for (int i = 0; i < response.getMembers().size(); i++) {
            if (response.getMembers().get(i).getId().equals(AuthUtil.getLoggedInUserId())) {
                ownIndex = i;
                break;
            }
        }
        // The 4-person view is as 0,3,1,2, so swap array to make sequential turn
        Collections.rotate(response.getMembers(), -ownIndex);
        if (response.getMembers().size() == 4) {
            Collections.swap(response.getMembers(), 1, 3);
            Collections.swap(response.getMembers(), 2, 1);
        }
        return response;
    }

    @Override
    @Transactional
    public RoomResponse addBot(String roomId, Integer level) {
        RoomEntity roomEntity = roomRepository.findById(roomId).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        if (!roomEntity.getOwner().getId().equals(AuthUtil.getLoggedInUserId())) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "You are not owner of this room");
        }
        if (roomEntity.getMembers().size() + roomEntity.getAgent().size() >= roomMemberMax) {
            throw new BadRequestException(ErrorCodes.ROOM_FULL, "Room is full");
        }
        Map<String, String> detail = new HashMap<>();
        roomEntity.getAgent().add(level);
        detail.put("name", "Level " + level + " Agent");
        updaterService.updateRoom(roomEntity.getId(), UpdateType.PLAYER_JOINED, detail);
        return roomMapper.toResponse(roomRepository.save(roomEntity));
    }

    @Override
    @Transactional
    public void removeFromRoom(String id, String userId) {
        RoomEntity roomEntity = roomRepository.findById(id).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Room not found"));
        if (!roomEntity.getOwner().getId().equals(AuthUtil.getLoggedInUserId())) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "You are not owner of this room");
        }
        Map<String, String> detail = new HashMap<>();
        detail.put("id", userId);
        if (userId.startsWith("BOT")) {
            String[] agent = userId.split(AgentConstant.AGENT_ID_SEPARATOR);
            Integer agentLevel = Integer.parseInt(agent[1]);
            for (int i = 0; i < roomEntity.getAgent().size(); i++) {
                if (roomEntity.getAgent().get(i).equals(agentLevel)) {
                    roomEntity.getAgent().remove(i);
                    break;
                }
            }
            detail.put("name", "Level " + agentLevel + " Agent");
        } else {
            for (UserEntity entity : roomEntity.getMembers()) {
                if (entity.getId().equals(userId)) {
                    detail.put("name", entity.getName());
                    break;
                }
            }
            roomEntity.getMembers().removeIf(user -> user.getId().equals(userId));
        }
        roomRepository.save(roomEntity);
        updaterService.updateRoom(roomEntity.getId(), UpdateType.PLAYER_KICKED, detail);
    }
}

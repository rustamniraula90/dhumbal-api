package com.fyp.dhumbal.room.mapper;

import com.fyp.dhumbal.room.dal.RoomEntity;
import com.fyp.dhumbal.room.dal.member.RoomMemberEntity;
import com.fyp.dhumbal.room.rest.model.RoomResponse;
import com.fyp.dhumbal.user.dal.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "code", source = "code")
    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "privateRoom", source = "privateRoom")
    RoomEntity toEntity(String code, UserEntity owner, boolean privateRoom);

    @Mapping(target = "ownerId", source = "owner.id")
    RoomMemberEntity toRoomMember(RoomEntity room);

    @Mapping(target = "owner.email", ignore = true)
    RoomResponse toResponse(RoomEntity roomEntity);
}

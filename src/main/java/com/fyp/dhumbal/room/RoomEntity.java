package com.fyp.dhumbal.room;

import com.fyp.dhumbal.global.entity.BaseEntity;
import com.fyp.dhumbal.user.dal.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
public class RoomEntity extends BaseEntity {

    @Column(name = "room_code", unique = true)
    private String roomCode;

    private boolean privateRoom = false;

    @Column(name = "room_status")
    @Enumerated(EnumType.STRING)
    private RoomStatusEnum status = RoomStatusEnum.CREATED;

    @OneToOne
    private UserEntity owner;

}

package com.fyp.dhumbal.room.dal;

import com.fyp.dhumbal.global.entity.BaseEntity;
import com.fyp.dhumbal.user.dal.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "rooms")
@Getter
@Setter
public class RoomEntity extends BaseEntity {

    @Column(name = "code", unique = true)
    private String code;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private RoomStatusEnum status = RoomStatusEnum.WAITING;

    @OneToOne
    private UserEntity owner;

    private boolean privateRoom = false;

}

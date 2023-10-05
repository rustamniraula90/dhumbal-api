package com.fyp.dhumbal.room.dal.member;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("room_member")
public class RoomMemberEntity implements Serializable {
    @Id
    private String id;
    @Id
    private String code;
    @Indexed
    private String ownerId;
    @Indexed
    private List<String> members;
}

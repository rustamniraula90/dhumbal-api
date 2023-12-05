package com.fyp.dhumbal.friend.dal;

import com.fyp.dhumbal.global.entity.BaseEntity;
import com.fyp.dhumbal.user.dal.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "friend")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendEntity extends BaseEntity {
    @ManyToOne
    private UserEntity user1;
    @ManyToOne
    private UserEntity user2;
    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;

}

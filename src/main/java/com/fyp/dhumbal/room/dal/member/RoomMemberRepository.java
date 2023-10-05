package com.fyp.dhumbal.room.dal.member;

import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomMemberRepository extends KeyValueRepository<RoomMemberEntity, String> {
}

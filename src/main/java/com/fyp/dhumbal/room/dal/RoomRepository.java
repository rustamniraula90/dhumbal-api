package com.fyp.dhumbal.room.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, String> {
    boolean existsByCode(String code);

    Optional<RoomEntity> findByCodeAndStatus(String code, RoomStatusEnum status);

    List<RoomEntity> findByStatusAndPrivateRoom(RoomStatusEnum roomStatusEnum, boolean privateRoom);
}

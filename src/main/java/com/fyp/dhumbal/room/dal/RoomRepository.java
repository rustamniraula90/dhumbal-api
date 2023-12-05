package com.fyp.dhumbal.room.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity, String> {
    boolean existsByCode(String code);

    Optional<RoomEntity> findByCodeAndStatus(String code, RoomStatusEnum status);

    Optional<RoomEntity> findByCode(String code);

    Optional<RoomEntity> findByOwner_Id(String ownerId);

    List<RoomEntity> findByStatusAndPrivateRoom(RoomStatusEnum roomStatusEnum, boolean privateRoom);

    Optional<RoomEntity> findByMembers_Id(String memberId);
}

package com.fyp.dhumbal.friend.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<FriendEntity, String> {

    @Query("SELECT f FROM FriendEntity f WHERE (f.user1.id=?1 AND f.user2.id=?2) OR (f.user1.id=?2 AND f.user2.id=?1)")
    Optional<FriendEntity> findFriendship(String user1Id, String user2Id);

    List<FriendEntity> findByUser2_IdAndStatus(String userId, FriendshipStatus status);

    @Query("SELECT f FROM FriendEntity f WHERE (f.user1.id=?1 OR f.user2.id=?1) AND f.status=?2")
    List<FriendEntity> findFriendship(String userId, FriendshipStatus status);

    @Query("SELECT f FROM FriendEntity f WHERE ((f.user1.id=?1 AND f.user2.online=?3) OR (f.user2.id=?1 AND f.user1.online=?3)) AND f.status=?2")
    List<FriendEntity> findFriendship(String userId, FriendshipStatus status, boolean online);
}

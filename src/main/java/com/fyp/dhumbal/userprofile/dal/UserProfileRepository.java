package com.fyp.dhumbal.userprofile.dal;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfileEntity, String> {

    @Query("select u from UserProfileEntity u order by u.totalPoints desc")
    List<UserProfileEntity> findTopPlayer(Pageable pageable);

    @Query("SELECT COUNT(u) + 1 FROM UserProfileEntity u WHERE u.totalPoints > :userPoints")
    Long getUserRankByTotalPoints(@Param("userPoints") int userPoints);

}

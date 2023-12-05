package com.fyp.dhumbal.user.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByExternalId(String externalId);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByOnlineAndLastOnlineLessThan(boolean online, long lastOnline);
}

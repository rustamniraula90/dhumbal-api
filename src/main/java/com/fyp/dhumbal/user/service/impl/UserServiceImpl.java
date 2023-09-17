package com.fyp.dhumbal.user.service.impl;

import com.fyp.dhumbal.global.sdk.GoogleSdk;
import com.fyp.dhumbal.user.dal.UserEntity;
import com.fyp.dhumbal.user.dal.UserRepository;
import com.fyp.dhumbal.user.mapper.UserMapper;
import com.fyp.dhumbal.user.rest.model.UserResponse;
import com.fyp.dhumbal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserEntity getOrCreateUser(GoogleSdk.GoogleUserDetail googleUserDetail) {
        return userRepository.findByExternalId(googleUserDetail.getId())
                .orElseGet(() -> createGoogleUser(googleUserDetail));
    }

    @Override
    public UserEntity createNewGuest() {
        return userRepository.save(userMapper.newGuest());
    }

    @Override
    public UserResponse getById(String loggedInUserId) {
        return userMapper.toResponse(userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("user not found")));
    }

    private UserEntity createGoogleUser(GoogleSdk.GoogleUserDetail googleUserDetail) {
        log.info("saving new user with id {}", googleUserDetail.getId());
        return userRepository.save(userMapper.toEntity(googleUserDetail));
    }
}

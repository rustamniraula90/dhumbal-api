package com.fyp.dhumbal.user.service.impl;

import com.fyp.dhumbal.auth.rest.model.LoginRequest;
import com.fyp.dhumbal.auth.rest.model.RegisterRequest;
import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.BadRequestException;
import com.fyp.dhumbal.global.sdk.GoogleSdk;
import com.fyp.dhumbal.user.dal.UserEntity;
import com.fyp.dhumbal.user.dal.UserRepository;
import com.fyp.dhumbal.user.mapper.UserMapper;
import com.fyp.dhumbal.user.rest.model.UserResponse;
import com.fyp.dhumbal.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


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
                .orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "user not found")));
    }

    @Override
    public UserEntity loginUser(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "Invalid username or password"));
        if (passwordEncoder.matches(request.getPassword(), user.getPassword()))
            return user;
        else
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Invalid username or password");
    }

    @Override
    public UserEntity registerUser(RegisterRequest request) {
        UserEntity user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }

    private UserEntity createGoogleUser(GoogleSdk.GoogleUserDetail googleUserDetail) {
        log.info("saving new user with id {}", googleUserDetail.getId());
        return userRepository.save(userMapper.toEntity(googleUserDetail));
    }
}

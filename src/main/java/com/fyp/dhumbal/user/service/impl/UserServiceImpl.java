package com.fyp.dhumbal.user.service.impl;

import com.fyp.dhumbal.auth.rest.model.LoginRequest;
import com.fyp.dhumbal.auth.rest.model.RegisterRequest;
import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.BadRequestException;
import com.fyp.dhumbal.global.sdk.GoogleSdk;
import com.fyp.dhumbal.global.service.EmailService;
import com.fyp.dhumbal.global.util.RandomGenerator;
import com.fyp.dhumbal.user.dal.UserEntity;
import com.fyp.dhumbal.user.dal.UserRepository;
import com.fyp.dhumbal.user.mapper.UserMapper;
import com.fyp.dhumbal.user.rest.model.UserResponse;
import com.fyp.dhumbal.user.service.UserService;
import com.fyp.dhumbal.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileService userProfileService;
    private final EmailService emailService;

    @Value("${dhumbal.security.otp.expiry}")
    private int otpExpiryTimeSecond;

    @Value("${dhumbal.security.otp.length}")
    private int otpLength;

    @Override
    public UserEntity getOrCreateUser(GoogleSdk.GoogleUserDetail googleUserDetail) {
        return userRepository.findByExternalId(googleUserDetail.getId())
                .orElseGet(() -> createGoogleUser(googleUserDetail));
    }

    @Override
    public UserEntity createNewGuest() {
        return createUser(userMapper.newGuest());
    }

    @Override
    public UserResponse getById(String loggedInUserId, boolean includeEmail) {
        return userMapper.toResponse(userRepository.findById(loggedInUserId).map(user -> {
            if (!includeEmail) user.setEmail(null);
            return user;
        }).orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "user not found")));
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
        user.setOtp(RandomGenerator.generateAlphanumeric(otpLength));
        user.setOtpExpiry(System.currentTimeMillis() + (otpExpiryTimeSecond * 1000L));
        user = createUser(user);
        try {
            emailService.sendOtpEmail(user.getId(), user.getName(), user.getEmail(), user.getOtp());
        } catch (Exception e) {
            log.warn("Error while sending email to " + user.getEmail(), e);
        }
        return user;
    }

    @Override
    public void verifyUser(String id, String code) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorCodes.BAD_REQUEST, "User not found!!"));
        if (!user.getOtp().equals(code)) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Invalid verification code!!");
        }
        if (user.getOtpExpiry() > System.currentTimeMillis()) {
            throw new BadRequestException(ErrorCodes.BAD_REQUEST, "Code has expired!!");
        }
        user.setVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);
    }

    private UserEntity createGoogleUser(GoogleSdk.GoogleUserDetail googleUserDetail) {
        return createUser(userMapper.toEntity(googleUserDetail));
    }

    private UserEntity createUser(UserEntity userEntity) {
        userEntity = userRepository.save(userEntity);
        userProfileService.createUserProfile(userEntity);
        return userEntity;
    }
}

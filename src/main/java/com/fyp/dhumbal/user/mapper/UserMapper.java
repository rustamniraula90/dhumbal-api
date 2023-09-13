package com.fyp.dhumbal.user.mapper;

import com.fyp.dhumbal.global.sdk.GoogleSdk;
import com.fyp.dhumbal.global.util.RandomGenerator;
import com.fyp.dhumbal.user.dal.UserEntity;
import com.fyp.dhumbal.user.dal.UserType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", source = "id")
    @Mapping(target = "userType", constant = "PLAYER")
    @Mapping(target = "verified", constant = "true")
    UserEntity toEntity(GoogleSdk.GoogleUserDetail googleUserDetail);


    default UserEntity newGuest() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserType(UserType.GUEST);
        userEntity.setName("Guest_" + RandomGenerator.generateAlphanumeric(10));
        return userEntity;
    }
}

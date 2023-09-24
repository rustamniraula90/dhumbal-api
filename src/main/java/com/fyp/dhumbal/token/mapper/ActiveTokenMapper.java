package com.fyp.dhumbal.token.mapper;

import com.fyp.dhumbal.global.service.RequestDataProvider;
import com.fyp.dhumbal.token.entity.ActiveTokenEntity;
import com.fyp.dhumbal.token.entity.ActiveTokenType;
import com.fyp.dhumbal.user.rest.model.UserSessionResponse;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {RequestDataProvider.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class ActiveTokenMapper {

    @Autowired
    protected RequestDataProvider requestDataProvider;

    @Mapping(target = "ip", expression = "java(requestDataProvider.getRequestIp())")
    @Mapping(target = "userAgent", expression = "java(requestDataProvider.getRequestUserAgent())")
    @Mapping(target = "user", source = "userId")
    @Mapping(target = "loggedInTime", source = "loggedInTime")
    public abstract ActiveTokenEntity toEntity(String id, String userId, LocalDateTime expiry, ActiveTokenType tokenType, LocalDateTime loggedInTime);

    public abstract List<UserSessionResponse> toSessionResponse(List<ActiveTokenEntity> activeTokenEntities);

}

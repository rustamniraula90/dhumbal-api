package com.fyp.dhumbal.global.service;


import com.fyp.dhumbal.global.validation.BaseValidation;

public abstract class BaseService {

    public <T> void validate(T object) {
        new BaseValidation<>().validate(object);
    }
}

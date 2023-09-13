package com.fyp.dhumbal.global.validation;

import com.fyp.dhumbal.global.error.codes.ErrorCodes;
import com.fyp.dhumbal.global.error.exception.impl.BadRequestException;
import com.fyp.dhumbal.global.error.model.ValidationError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
public class BaseValidation<T> {

    private final List<ValidationError> errors = new ArrayList<>();


    public BaseValidation(BindException ex) {
        for (ObjectError error : ex.getAllErrors()) {
            errors.add(ValidationError.builder().field(error.getObjectName()).message(error.getDefaultMessage()).build());
        }
    }

    public static void sendValidationError(List<ValidationError> errors) {
        throw new BadRequestException(ErrorCodes.BAD_REQUEST,
                "Validation Failed. Please check the data and try again.", errors);
    }

    public static void sendValidationError(ValidationError errors) {
        sendValidationError(Collections.singletonList(errors));
    }

    public void validate(T objectToValidate) {
        constraintViolationValidation(objectToValidate);
        if (!errors.isEmpty())
            sendValidationError(errors);
    }

    private void checkError(List<ValidationError> errors) {
        if (errors == null || errors.isEmpty()) return;
        this.errors.addAll(errors);
    }

    private void constraintViolationValidation(T objectToValidate) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(objectToValidate);
        violations.forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.add(ValidationError.builder().field(propertyPath).message(message).build());
        });
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}

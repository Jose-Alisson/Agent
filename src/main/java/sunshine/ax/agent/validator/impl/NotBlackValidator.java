package com.sunshineax.agent.validator.impl;

import com.sunshineax.agent.validator.RequestValidator;
import com.sunshineax.agent.validator.annotations.NotBlack;
import com.sunshineax.agent.validator.annotations.NotNull;

import java.lang.reflect.Field;

public class NotBlackValidator implements RequestValidator<NotBlack> {

    @Override
    public Class<NotBlack> getType() {
        return NotBlack.class;
    }

    @Override
    public void validate(Field field, Object instance) throws IllegalAccessException {
        Object value = field.get(instance);
        NotBlack notBlack = field.getAnnotation(NotBlack.class);

        if(value == null){
            throw new RuntimeException("Required field");
        }

        if (value instanceof String stringValue) {
            if(stringValue.isBlank()){
                throw new RuntimeException(notBlack.message());
            }
        }
    }
}

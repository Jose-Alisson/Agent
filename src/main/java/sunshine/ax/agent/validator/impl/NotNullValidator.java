package com.sunshineax.agent.validator.impl;

import com.sunshineax.agent.validator.RequestValidator;
import com.sunshineax.agent.validator.annotations.NotNull;
import com.sunshineax.agent.validator.execption.InvokerValidatorException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class NotNullValidator implements RequestValidator<NotNull> {

    @Override
    public Class<NotNull> getType() {
        return NotNull.class;
    }

    @Override
    public void validate(Field field, Object instance) throws InvokerValidatorException, IllegalAccessException {
        NotNull annotation = field.getAnnotation(NotNull.class);
        Object value = field.get(instance);

        if (value == null) {
            throw new NullPointerException(annotation.message());
        }
    }
}

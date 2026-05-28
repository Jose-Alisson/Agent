package com.sunshineax.agent.validator;

import com.sunshineax.agent.validator.execption.InvokerValidatorException;

import java.lang.reflect.Field;

public interface RequestValidator<T> {
    Class<T> getType();
    void validate(Field field, Object instance) throws InvokerValidatorException, IllegalAccessException;
}

package com.sunshineax.agent.validator.impl;

import com.sunshineax.agent.validator.RequestValidator;
import com.sunshineax.agent.validator.annotations.Length;
import com.sunshineax.agent.validator.execption.InvokerValidatorException;

import java.lang.reflect.Field;

public class LengthValidator implements RequestValidator<Length> {
    @Override
    public Class<Length> getType() {
        return Length.class;
    }

    @Override
    public void validate(Field field, Object instance) throws InvokerValidatorException, IllegalAccessException {
        Object value = field.get(instance);
        Length length = field.getAnnotation(Length.class);

        if(value instanceof String valueString){
            int lengthValue = valueString.length();
            if(lengthValue < length.min() || lengthValue > length.max()){
                throw new RuntimeException(length.message());
            }
        }
    }
}

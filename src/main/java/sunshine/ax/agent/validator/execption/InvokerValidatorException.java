package com.sunshineax.agent.validator.execption;

import java.lang.reflect.Field;

public class InvokerValidatorException extends RuntimeException {

    private Object value;

    public InvokerValidatorException(Field field, String message) {
        super(message);
    }

    public InvokerValidatorException(String message, Object value) {
        super(message);
        this.value = value;
    }

    public Object getValue() {
        return value;
    }
}

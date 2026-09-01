package sunshineax.agent.validator;

import sunshineax.agent.exception.InvokerValidatorException;

import java.lang.reflect.Field;

public interface RequestValidator<T> {
    Class<T> getType();
    void validate(Field field, Object instance) throws InvokerValidatorException, IllegalAccessException;
}

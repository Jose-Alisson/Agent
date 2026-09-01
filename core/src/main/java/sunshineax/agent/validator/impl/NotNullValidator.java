package sunshineax.agent.validator.impl;

import sunshineax.agent.validator.RequestValidator;
import sunshineax.agent.validator.annotations.NotNull;
import sunshineax.agent.exception.InvokerValidatorException;

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

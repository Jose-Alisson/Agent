package sunshineax.agent.validator.impl;

import sunshineax.agent.validator.RequestValidator;
import sunshineax.agent.validator.annotations.Size;
import sunshineax.agent.exception.InvokerValidatorException;

import java.lang.reflect.Field;

public class SizeValidator implements RequestValidator<Size> {
    @Override
    public Class<Size> getType() {
        return Size.class;
    }

    @Override
    public void validate(Field field, Object instance) throws InvokerValidatorException, IllegalAccessException {
        Object value = field.get(instance);
        Size annotation = field.getAnnotation(Size.class);

        if(value == null){
            throw new NullPointerException("Required field");
        }

        if(value instanceof Integer v){
            if(v < annotation.min() || v > annotation.max()){
                throw new RuntimeException(annotation.message());
            }
        }
    }
}

package sunshineax.agent.validator.impl;

import sunshineax.agent.validator.RequestValidator;
import sunshineax.agent.validator.annotations.Length;
import sunshineax.agent.exception.InvokerValidatorException;

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

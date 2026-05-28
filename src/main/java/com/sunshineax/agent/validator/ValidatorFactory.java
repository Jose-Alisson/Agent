package com.sunshineax.agent.validator;

import com.sunshineax.agent.validator.annotations.Length;
import com.sunshineax.agent.validator.annotations.NotBlack;
import com.sunshineax.agent.validator.annotations.NotNull;
import com.sunshineax.agent.validator.execption.InvokerValidatorException;
import com.sunshineax.agent.validator.impl.LengthValidator;
import com.sunshineax.agent.validator.impl.NotBlackValidator;
import com.sunshineax.agent.validator.impl.NotNullValidator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidatorFactory {

    private final Map<Class<?>, RequestValidator<?>> validators = Map.of(
            NotBlack.class, new NotBlackValidator(),
            NotNull.class, new NotNullValidator(),
            Length.class, new LengthValidator()
    );

    public <T> boolean validator(T t) throws InvokerValidatorException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<Map<Class<? extends Annotation>, Object>> allErrors = new ArrayList<>();
        Class<?> clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Map<Class<? extends Annotation>, Object> errors = getErrors(field, t, field.getAnnotations());

            if(!errors.isEmpty()){
                allErrors.add(errors);
            }
        }

        if(!allErrors.isEmpty()){
            throw new InvokerValidatorException("All errors fields",  allErrors);
        }

        return true;
    }

    private Map<Class<? extends Annotation>, Object> getErrors(Field field, Object instance, Annotation[] annotations) {
        Map<Class<? extends Annotation>, Object> errors = new HashMap<>();

        for (Annotation annotation : annotations) {
            RequestValidator<?> validator = validators.get(annotation.annotationType());
            if (validator != null) {
                try {
                    validator.validate(field, instance);
                } catch (Exception e) {
                    errors.put(annotation.annotationType(), e.getMessage());
                }
            }
        }
        return errors;
    }
}

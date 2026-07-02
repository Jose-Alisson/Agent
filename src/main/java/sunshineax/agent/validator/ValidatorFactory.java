package sunshineax.agent.validator;

import sunshineax.agent.validator.annotations.Length;
import sunshineax.agent.validator.annotations.NotBlack;
import sunshineax.agent.validator.annotations.NotNull;
import sunshineax.agent.exception.InvokerValidatorException;
import sunshineax.agent.validator.impl.LengthValidator;
import sunshineax.agent.validator.impl.NotBlackValidator;
import sunshineax.agent.validator.impl.NotNullValidator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

    public <T> boolean validator(T t) throws InvokerValidatorException {
        Map<String, List<ValidatorConstraint>> errors = new HashMap<>();
        Class<?> clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            List<ValidatorConstraint> constraints = getErrors(field, t, field.getAnnotations());

            if (!constraints.isEmpty()) {
                errors.put(field.getName(), constraints);
            }
        }

        if(!errors.isEmpty()){
            throw new InvokerValidatorException("Validator Object " + t.getClass().getSimpleName(),  errors);
        }

        return true;
    }

    private List<ValidatorConstraint> getErrors(Field field, Object instance, Annotation[] annotations) {
        List<ValidatorConstraint> constraints = new ArrayList<>();

        for (Annotation annotation : annotations) {
            RequestValidator<?> validator = validators.get(annotation.annotationType());

            if (validator != null) {
                try {
                    validator.validate(field, instance);
                } catch (Exception e) {
                    String message = "";
                    try {
                        Method messageMethod = annotation.getClass().getMethod("message");
                        message = messageMethod.invoke(annotation).toString();
                    } catch (Exception _){}
                    constraints.add(new ValidatorConstraint(annotation.annotationType().getSimpleName(), message));
                }
            }
        }

        return constraints;
    }
}

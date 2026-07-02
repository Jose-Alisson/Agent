package sunshineax.agent.validator.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Name("Size")
public @interface Size {
    int min() default 0;
    int max() default Integer.MAX_VALUE;
    String message() default "";
}

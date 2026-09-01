package sunshineax.agent.annotations;

import sunshineax.agent.enums.TypeIntent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Capability {
    String intent();
    TypeIntent type() default TypeIntent.EVENT;
}

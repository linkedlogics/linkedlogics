package dev.linkedlogics.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Input {
    String value();
    boolean required() default false;
    String description() default "";
}

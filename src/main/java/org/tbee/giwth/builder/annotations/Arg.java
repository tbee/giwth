package org.tbee.giwth.builder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes that an action parameter should be treated as an argument to the action's method.
 * It will not get an setter.
 *
 * Instead of ...action().value(x) the user must call ...action(x)
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Arg {
    public String name() default "";
}

package org.tbee.giwth.builder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can be added to a variable at instance level and will create an additonal of*() method for instantiating the builder.
 * So instead of Step.of().var(x) the user may call Step.ofVar(x).
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Of {
    public String name() default "";
}

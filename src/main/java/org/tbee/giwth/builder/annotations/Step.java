package org.tbee.giwth.builder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE) // this is class
@Retention(RetentionPolicy.SOURCE)
public @interface Step {
    public String stripSuffix() default "";
}

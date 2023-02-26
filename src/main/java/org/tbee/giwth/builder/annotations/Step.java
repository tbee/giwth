package org.tbee.giwth.builder.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate that this class should be extended using the GiWTh builder assistant.
 * This means:
 * <ul>
 * <li>all instances variable become step parameters</li>
 * <li>all subclasses become actions returning Given/When/Then</li>
 * <li>all instance variables inside subclases become action parameters or arguments</li>
 * </ul>
 */
@Target(ElementType.TYPE) // this is class
@Retention(RetentionPolicy.SOURCE)
public @interface Step {
    public String stripSuffix() default "";
}

package net.alperemre.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks model variables that should be bound for scripts or other models.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Bind {
}

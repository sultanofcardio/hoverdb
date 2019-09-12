package com.sultanofcardio.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify the name of the database table represented by this entity. Your class must extend the class {@link Entity}
 * for this annotation to have any effect
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface EntityName {

    /**
     * Sets the name of the database table
     * @return The name of the database table
     */
    String value();
}

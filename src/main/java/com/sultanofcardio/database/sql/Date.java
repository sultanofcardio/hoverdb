package com.sultanofcardio.database.sql;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to map a field in a class to a database date field.
 * This is automatically detected, but uses the date format yyyy-MM-dd hh:mm:ss by default.
 * You may use a custom format by setting the value of this annotation
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface Date {

    String DEFAULT_FORMAT = "yyyy-MM-dd hh:mm:ss";

    /**
     * The date DEFAULT_FORMAT to use for date parsing
     * @return The value of the date DEFAULT_FORMAT
     */
    String value() default DEFAULT_FORMAT;
}

package com.tcc.distributedtransaction.annotation.client;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cancel {

    @AliasFor("value")
    String name() default "";

    @AliasFor("name")
    String value() default "";

    String before() default "";

    String after() default "";
}

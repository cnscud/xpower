package com.cnscud.xpower.knife.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
/**
 * 表名称
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-09-23
 */
public @interface Table {
    /**表名称*/
    String value() default "";
}

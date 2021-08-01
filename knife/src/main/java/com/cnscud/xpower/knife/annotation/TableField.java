package com.cnscud.xpower.knife.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
/**
 * 表字段
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-09-23
 */
public @interface TableField {
    /**字段名称*/
    String value() default "";
    /**是否是数据库字段*/
    boolean yes() default true;
    /**是否主键ID*/
    boolean id() default false;
}

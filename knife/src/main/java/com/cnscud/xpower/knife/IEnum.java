package com.cnscud.xpower.knife;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 枚举类型
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-09-23
 */
public interface IEnum<T> {

    T getValue();
    
    default boolean same(Object value) {
        final Object me = this.getValue();
        if(value instanceof Integer && me instanceof Number) {
            //加速整数的比较
            return value != null && me != null && Objects.equals(value, me);
        }
        if(me instanceof Integer && value instanceof String) {
            return value != null && me != null && String.valueOf(me).equals(value);
        }
        if(value instanceof Number && me instanceof Number) {
            return new BigDecimal(String.valueOf(me)).compareTo(new BigDecimal(String.valueOf(value))) == 0;
        }
        return Objects.equals(me, value);
    }
}

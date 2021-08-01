package com.cnscud.xpower.knife;

import com.fasterxml.jackson.annotation.JsonValue;


/**
 * @author mie 2021-02-07 22:11
 * @version 1.0.0
 */
public interface IntEum extends IEnum<Integer> {
    @JsonValue
    Integer getValue();
    String getDesc();

    static <T extends IntEum> T create(Class<T> clazz, Object o){
        for(T e: clazz.getEnumConstants()) {
            if(e.same(o)) {
                return e;
            }
        }
        return null;
    }
}

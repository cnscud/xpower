package com.cnscud.xpower.knife;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author mie peng 2021-02-22 11:20
 * @version 1.0.0
 */
@FunctionalInterface
public interface IFunction<T,R> extends Function<T,R>, Serializable {
    R apply(T t);
}

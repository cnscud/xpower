package com.cnscud.xpower.utils;

/**
 * 自动解包解决空指针问题
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2017年8月21日
 */
public class Unboxs {

    public static int unbox(Integer v, int defaultValue) {
        return v == null ? defaultValue : v.intValue();
    }

    public static int unbox(Integer v) {
        return unbox(v, 0);
    }

    public static long unbox(Long v, long defaultValue) {
        return v == null ? defaultValue : v.longValue();
    }

    public static long unbox(Long v) {
        return unbox(v, 0);
    }

    public static boolean unbox(Boolean v, boolean defaultValue) {
        return v == null ? defaultValue : v.booleanValue();
    }

    public static boolean unbox(Boolean v) {
        return v.booleanValue();
    }

    public static double unbox(Double v, double defaultValue) {
        return v == null ? defaultValue : v.doubleValue();
    }

    public static double unbox(Double v) {
        return unbox(v, 0);
    }

    public static short unbox(Short v, short defaultValue) {
        return v == null ? defaultValue : v.shortValue();
    }

    public static short unbox(Short v) {
        return unbox(v, (short) 0);
    }

    public static float unbox(Float v, float defaultValue) {
        return v == null ? defaultValue : v.floatValue();
    }

    public static float unbox(Float v) {
        return unbox(v, 0);
    }
}

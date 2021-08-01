package com.cnscud.xpower.dao;

import static java.lang.String.format;
import static java.lang.String.join;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * 包含自动返回自增id的操作方法<br/>
 * 目前支持{@link String}、{@link Long}、{@link Integer}
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-6-14
 */
public class OpInsert<T> extends OpUpdate {

    /**
     * 返回结果的类型，目前支持{@link String}、{@link Long}、{@link Integer}
     */
    public final Class<T> clazz;

    final Class<?>[] supportedClass = { Long.class, Integer.class, String.class };

    public OpInsert(CharSequence sql, String bizName, long index, Class<T> clazz) {
        super(sql, bizName, index);
        this.clazz = clazz;
        checkClazz(clazz);
    }

    public OpInsert(CharSequence sql, String bizName, Class<T> clazz) {
        super(sql, bizName);
        this.clazz = clazz;
        checkClazz(clazz);
    }

    protected void checkClazz(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("the class must not be null");
        }
        for (Class<?> checkClass : supportedClass) {
            if (clazz == checkClass) {
                return;
            }
        }
        throw new IllegalArgumentException("supported class type: " + clazz + ", only supported: " + Arrays.toString(supportedClass));
    }

    @Override
    public OpInsert<T> addParams(Object... params) {
        super.addParams(params);
        return this;
    }

    @Override
    public OpInsert<T> setRoutePattern(String routePattern) {
        super.setRoutePattern(routePattern);
        return this;
    }

    public T parsePrimaryKey(ResultSet rs) throws SQLException {
        if (rs != null && rs.next()) {
            if (clazz.isAssignableFrom(Long.class)) {
                return (T) new Long(rs.getLong(1));
            }
            if (clazz.isAssignableFrom(String.class)) {
                return (T) rs.getString(1);
            }
            if (clazz.isAssignableFrom(Integer.class)) {
                return (T) new Integer(rs.getInt(1));
            }
        }
        return (T) null;
    }
}
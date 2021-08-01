package com.cnscud.xpower.knife.impl;

import static java.lang.String.format;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.*;

import com.cnscud.xpower.knife.IEnum;
import com.cnscud.xpower.knife.annotation.Table;
import com.cnscud.xpower.knife.annotation.TableField;
import com.cnscud.xpower.dao.FieldValue;
import com.cnscud.xpower.dao.SqlWhere;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-09-23
 */
public class TableInfo{
    final Class<?> clazz;
    //
    final String tableName;
    final List<TableFieldInfo> fieldInfos = new ArrayList<>();
    final TableFieldInfo primaryKey;

    public String getTableName(){
        return tableName;
    }

    TableInfo(Class<?> clazz) {
        this.clazz = clazz;
        Table table = clazz.getAnnotation(Table.class);
        Objects.requireNonNull(table, format("%s without `Table` annotation", clazz));
        //
        this.tableName = table.value();
        //
        Class<?> theClass = clazz;
        while(theClass != null && theClass != Object.class) {
           for(Field f : theClass.getDeclaredFields()) {
               if(!Modifier.isStatic(f.getModifiers()) //
                       && !Modifier.isFinal(f.getModifiers())//
                       && !Modifier.isTransient(f.getModifiers())
                       ) {
                   TableField tf = f.getAnnotation(TableField.class);
                   if(tf == null || tf.yes()) {
                       fieldInfos.add(new TableFieldInfo(f, tf));
                   }
               }
           }
           theClass = theClass.getSuperclass();
        }
        //
        primaryKey = fieldInfos.stream().filter(TableFieldInfo::isPrimaryKey).findFirst().orElse(null);
    }
    //
    <T> T newObject() throws SQLException{
        try {
            return (T) clazz.newInstance();
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
    TableFieldInfo getField(String column) {
        for(TableFieldInfo f: fieldInfos) {
            if(f.column.equalsIgnoreCase(column)) {
                return f;
            }
        }
        return null;
    }
    //
    void prepareUpdateById(Object obj, SqlWhere where){
        Objects.requireNonNull(primaryKey, "object without `TableField(id=true)`");
        final Object primaryKeyValue = primaryKey.getValue(obj);
        Objects.requireNonNull(primaryKeyValue, "value of `TableField(id=true)` must not be null");
        //
        boolean first = true;
        for(TableFieldInfo f: fieldInfos) {
           Object value = f.getValue(obj);
           if(!f.isPrimaryKey() && value != null) {
               value = convertEnumValue(value);
               if(first) {
                   first = false;
                   where.append(format(" %s=?", f.column), value);
               }else {
                   where.append(format(",%s=?", f.column), value);
               }
           }
        }
        where.append(format(" where %s=?", primaryKey.column), primaryKeyValue);
    }
    void prepareWhere(Object obj, SqlWhere where) {
        for(TableFieldInfo f: fieldInfos) {
            Object value = convertEnumValue(f.getValue(obj));
            where.andEquals(value != null, f.column, value);
        }
    }
    void prepareWhere(Map<String, Object> conditions, SqlWhere where) {
        conditions.forEach((k,v)->{
            where.andEquals(k, v);
        });
    }
    private Object convertEnumValue(Object value) {
        if (value instanceof IEnum<?>) {
            IEnum<?> ienum = (IEnum<?>) value;
            return ienum.getValue();
        }
        if (value instanceof Enum<?>) {
            Enum<?> en = (Enum<?>) value;
            return en.ordinal();
        }
        return value;
    }

    public List<Object> fieldValue(Object obj){
        List<Object> values = new LinkedList<>();
        for(TableFieldInfo f: fieldInfos) {
            Object value = convertEnumValue(f.getValue(obj));
            if(value != null) {
                values.add(value);
            }
        }
        return values;
    }

    public List<String> fieldName(){
        List<String> keys = new LinkedList<>();
        fieldInfos.forEach(e->keys.add(e.column));
        return keys;
    }


    
    FieldValue insertFields(Object obj){
        FieldValue fv = new FieldValue();
        for(TableFieldInfo f: fieldInfos) {
            Object value = convertEnumValue(f.getValue(obj));
            if(value != null) {
                fv.put(f.column, value);
            }
        }
        return fv;
    }
}

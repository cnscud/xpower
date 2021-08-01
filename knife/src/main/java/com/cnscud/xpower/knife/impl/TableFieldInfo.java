package com.cnscud.xpower.knife.impl;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;

import com.cnscud.xpower.knife.IEnum;
import com.cnscud.xpower.knife.annotation.TableField;
import com.cnscud.xpower.dao.SqlUtils;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-09-23
 */
public class TableFieldInfo {
    final Field field;
    final TableField tableField;
    //
    /** 数据库字段名称 */
    public final String column;



    public TableFieldInfo(Field field, TableField tableField) {
        this.field = field;
        this.tableField = tableField;
        if (tableField != null && tableField.value() != null && tableField.value().length() > 0) {
            this.column = tableField.value();
        } else {
            StringBuilder columnChars = new StringBuilder();
            for (char ch : field.getName().toCharArray()) {
                if (ch >= 'A' && ch <= 'Z') {
                    columnChars.append('_').append((char) (ch + ('a' - 'A')));
                } else {
                    columnChars.append(ch);
                }
            }
            this.column = columnChars.toString();
        }
    }

    //
    public boolean isPrimaryKey() {
        return tableField != null && tableField.id();
    }

    public void setter(Object obj, ResultSet rs, int columnIndex) throws SQLException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            Class<?> fc = field.getType();
            if (fc == int.class || fc == Integer.class) {
                int value = rs.getInt(columnIndex);
                field.set(obj, rs.wasNull() && fc == Integer.class ? null : value);
            }else if (fc == long.class || fc == Long.class) {
                long value = rs.getLong(columnIndex);
                field.set(obj, rs.wasNull() && fc == Long.class ? null : value);
            }else if (fc == boolean.class || fc == Boolean.class) {
                boolean value = rs.getBoolean(columnIndex);
                field.set(obj, rs.wasNull() && fc == Boolean.class ? null : value);
            }else if (fc == double.class || fc == Double.class) {
                double value = rs.getDouble(columnIndex);
                field.set(obj, rs.wasNull() && fc == Double.class ? null : value);
            }else if (fc == float.class || fc == Float.class) {
                float value = rs.getFloat(columnIndex);
                field.set(obj, rs.wasNull() && fc == Float.class ? null : value);
            }else if (fc == short.class || fc == Short.class) {
                short value = rs.getShort(columnIndex);
                field.set(obj, rs.wasNull() && fc == Short.class ? null : value);
            }else if (fc == byte.class || fc == Byte.class) {
                byte value = rs.getByte(columnIndex);
                field.set(obj, rs.wasNull() && fc == Byte.class ? null : value);
            }else if(IEnum.class.isAssignableFrom(fc) && Enum.class.isAssignableFrom(fc)) {//自定义枚举
                boolean isInt = ((IEnum)fc.getEnumConstants()[0]).getValue().getClass() == Integer.class;
                Object value = isInt ? rs.getInt(columnIndex) : rs.getObject(columnIndex);
                //此处注意tinyint等，mysql驱动会转换成boolean
                for(Object item: fc.getEnumConstants()) {
                    if(((IEnum)item).same(value)) {
                        field.set(obj, item);
                        break;
                    }
                }
            }else if(Enum.class.isAssignableFrom(fc)) {//普通枚举
                int enumIndex = rs.getInt(columnIndex);
                for(Object item: fc.getEnumConstants()) {
                    if(((Enum<?>)item).ordinal() == enumIndex) {
                        field.set(obj, item);
                        break;
                    }
                }
            }else if(fc == LocalDateTime.class) {
                field.set(obj, SqlUtils.getLocalDateTime(rs, columnIndex, null));
            }else if(fc == LocalDate.class) {
                field.set(obj, SqlUtils.getLocalDate(rs, columnIndex, null));
            }else if(fc == LocalTime.class) {
                field.set(obj, SqlUtils.getLocalTime(rs, columnIndex, null));
            }else if(fc == YearMonth.class) {
                field.set(obj, SqlUtils.getYearMonth(rs, columnIndex, null));
            }
            else {
                field.set(obj, rs.getObject(columnIndex));
            }
        } catch (IllegalArgumentException e) {
            throw new SQLException(e);
        } catch (IllegalAccessException e) {
            throw new SQLException(e);
        }
    }
    public Object getValue(Object obj){
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            return field.get(obj);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

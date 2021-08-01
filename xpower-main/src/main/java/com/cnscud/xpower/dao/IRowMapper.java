package com.cnscud.xpower.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * 单记录解析方法
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-1-30
 */
@FunctionalInterface
public interface IRowMapper<E> {
    /**
     * 解析一行记录
     * 
     * @param rs
     *            结果集
     * @param rowNum
     *            当前记录行数，从0开始
     * @return 解析的结果
     * @throws SQLException
     *             任何数据库异常
     */
    E mapRow(ResultSet rs, int rowNum) throws SQLException;

    /**
     * 获取Timestamp
     * 
     * @since 2015年4月21日
     * @see #getLocalDateTime(ResultSet, int, LocalDateTime)
     */
    default Date getTimestamp(ResultSet rs, int index, Date defaultDate) throws SQLException {
        return SqlUtils.getTimestamp(rs, index, defaultDate);
    }

    /**
     * 获取Timestamp
     * 
     * @since 2015年4月21日
     * @see #getLocalDateTime(ResultSet, String, LocalDateTime)
     */
    default Date getTimestamp(ResultSet rs, String columLabel, Date defaultDate) throws SQLException {
        return SqlUtils.getTimestamp(rs, columLabel, defaultDate);
    }

    /**
     * 获取Timestamp
     * 
     * @since 2015年4月21日
     */
    default LocalDateTime getLocalDateTime(ResultSet rs, int index, LocalDateTime defaultDateTime) throws SQLException {
        return SqlUtils.getLocalDateTime(rs, index, defaultDateTime);
    }

    /**
     * 获取Timestamp
     * 
     * @since 2015年4月21日
     */
    default LocalDateTime getLocalDateTime(ResultSet rs, String columLabel, LocalDateTime defaultDateTime) throws SQLException {
        return SqlUtils.getLocalDateTime(rs, columLabel, defaultDateTime);
    }

    default LocalDate getLocalDate(ResultSet rs, int index, LocalDate defaultDate) throws SQLException {
        return SqlUtils.getLocalDate(rs, index, defaultDate);
    }

    default LocalDate getLocalDate(ResultSet rs, String columLabel, LocalDate defaultDate) throws SQLException {
        return SqlUtils.getLocalDate(rs, columLabel, defaultDate);
    }

    default LocalTime getLocalTime(ResultSet rs, int index, LocalTime defaultTime) throws SQLException {
        return SqlUtils.getLocalTime(rs, index, defaultTime);
    }

    default LocalTime getLocalTime(ResultSet rs, String columLabel, LocalTime defaultTime) throws SQLException {
        return SqlUtils.getLocalTime(rs, columLabel, defaultTime);
    }

    public static IRowMapper<Integer> INTEGER = (rs, rn) -> rs.getInt(1);
    public static IRowMapper<Long> LONG = (rs, rn) -> rs.getLong(1);
    public static IRowMapper<String> STRING = (rs, rn) -> rs.getString(1);
    public static IRowMapper<Double> DOUBLE = (rs, rn) -> rs.getDouble(1);
}

package com.cnscud.xpower.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 一些SQL语句的辅助工具
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-10-31
 */
public final class SqlUtils {

    /**
     * 生成对个多个'?'
     * <p>
     * <ul>
     * <li>count=1: ?</li>
     * <li>count=2: ?,?</li>
     * <li>count=3: ?,?,?</li>
     * <li>count=9: ?,?,?,?,?,?,?,?,?,</li>
     * </ul>
     * </p>
     * 
     * @param count
     *            个数，必须>0
     * @return ？的语句，例如count=3时生成"?,?,?"
     */
    public static String buildQuestionMark(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must not be negative " + count);
        }
        StringBuilder buf = new StringBuilder(count);
        while (count-- > 0) {
            buf.append(',').append('?');
        }
        return buf.substring(1);
    }

    /**
     * 替换SQL语句中的命名参数为?列表
     * <p>
     * sql = 'SELECT * FROM ACCOUNT WHERE id IN (:ids)',name='ids',count=3, <br/>
     * 则生成: <br/>
     * 'SELECT * FROM ACCOUNT WHERE id IN (?,?,?)' </>
     * 
     * @param sql
     *            原始sql语句
     * @param name
     *            命名参数名称
     * @param count
     *            ?个数,count>0
     * @return 替换后的SQL语句
     */
    public static String replaceQuestionMark(CharSequence sql, String name, int count) {
        return sql.toString().replace(":" + name, buildQuestionMark(count));
    }

    /**
     * 替换SQL语句中的命名参数为?列表
     * <p>
     * sql = 'SELECT * FROM ACCOUNT WHERE id IN (:ids)',name='ids',count=3, <br/>
     * 则生成: <br/>
     * 'SELECT * FROM ACCOUNT WHERE id IN (?,?,?)' </>
     * 
     * @param builder
     *            原始sql语句，此StringBuilder会同步覆盖
     * @param name
     *            命名参数名称
     * @param count
     *            ?个数,count>0
     * @return 替换后的SQL语句
     * @author Ady Liu (imxylz@gmail.com)
     * @since 2013年7月8日
     */
    public static StringBuilder replaceQuestionMark(StringBuilder builder, String name, int count) {
        return builder.replace(0, builder.length(), replaceQuestionMark(builder.toString(), name, count));
    }

    /**
     * 设置参数
     * 
     * @param ps
     *            预编译语句
     * @param fromIndex
     *            起始索引
     * @param args
     *            参数列表
     * @return 下一个参数的索引
     */
    public static int setParams(PreparedStatement ps, final int fromIndex, Object... args) throws SQLException {
        int index = fromIndex;
        for (Object arg : args) {
            ps.setObject(index, arg);
            index++;
        }
        return index;
    }

    /**
     * 设置参数
     * 
     * @param ps
     *            预编译语句
     * @param fromIndex
     *            起始索引
     * @param args
     *            参数列表
     * @return 下一个参数的索引
     */
    public static int setParams(PreparedStatement ps, final int fromIndex, List<Object> args) throws SQLException {
        int index = fromIndex;
        for (Object arg : args) {
            ps.setObject(index, arg);
            index++;
        }
        return index;
    }

    /**
     * 计算数组所有元素的和
     * 还有一种 -2 的情况 也是成功
     * 
     * @param value
     *            数组
     * @return 数组所有元素和
     */
    public static int sum(int[] value) {
        int x = 0;
        for (int i = 0; i < value.length; i++) {
            x += value[i];
        }
        return x;
    }
    /**
     * 对于批量操作的 insert ... on duplication key update, 插入返回1，更新返回2，所以统一1和2返回1
     * @param value 数组
     * @return 数组的和
     * @since 2020-05-11
     */
    public static int sumBatchInsert(int[] value) {
        int x = 0;
        for (int i = 0; i < value.length; i++) {
            x += value[i] > 0 ? 1 : 0;
        }
        return x;
    }

    /**
     * 获取Timestamp
     * 
     * @since 2015年4月21日
     * @see #getLocalDate(ResultSet, int, LocalDate)
     * @see #getLocalDateTime(ResultSet, int, LocalDateTime)
     */
    public static Date getTimestamp(ResultSet rs, int index, Date defaultDate) throws SQLException {
        Timestamp t = rs.getTimestamp(index);
        return t == null ? defaultDate : new Date(t.getTime());
    }


    public static void main(String[] args) {
        System.out.println(new Date("1970-01-01 08:00:00").getTime() );
    }

    /**
     * 获取Timestamp
     * 
     * @since 2015年4月21日
     * @see #getLocalDate(ResultSet, String, LocalDate)
     * @see #getLocalDateTime(ResultSet, String, LocalDateTime)
     */
    public static Date getTimestamp(ResultSet rs, String columLabel, Date defaultDate) throws SQLException {
        Timestamp t = rs.getTimestamp(columLabel);
        return t == null ? defaultDate : new Date(t.getTime());
    }

    /**
     * 获取Timestamp
     * @param rs
     * @param columnLabel
     * @return
     * @throws SQLException
     */
    public static long getTimestamp(ResultSet rs,String columnLabel) throws SQLException {
        Timestamp t = rs.getTimestamp(columnLabel);
        return t == null ? 0 : t.getTime();
    }

    /**
     * 获取Timestamp
     * 
     * @since 2015年4月21日
     */
    public static LocalDateTime getLocalDateTime(ResultSet rs, int index, LocalDateTime defaultDateTime) throws SQLException {
        Timestamp t = rs.getTimestamp(index);
        return t == null ? defaultDateTime : LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
    }

    /**
     * 获取Timestamp
     * 
     * @since 2015年4月21日
     */
    public static LocalDateTime getLocalDateTime(ResultSet rs, String columLabel, LocalDateTime defaultDateTime) throws SQLException {
        Timestamp t = rs.getTimestamp(columLabel);
        return t == null ? defaultDateTime : LocalDateTime.ofInstant(t.toInstant(), ZoneId.systemDefault());
    }

    /**
     * 获取Date
     * 
     * @param rs
     *            ResultSet
     * @param index
     *            列索引
     * @param defaultDate
     *            默认日期
     * @return 日期
     * @throws SQLException
     *             sql异常
     */
    public static LocalDate getLocalDate(ResultSet rs, int index, LocalDate defaultDate) throws SQLException {
        java.sql.Date d = rs.getDate(index);
        return d == null ? defaultDate : d.toLocalDate();
    }

    /**
     * 获取Date
     * 
     * @param rs
     *            ResultSet
     * @param columLabel
     *            列名称
     * @param defaultDate
     * @return 日期
     * @throws SQLException
     *             sql异常
     */
    public static LocalDate getLocalDate(ResultSet rs, String columLabel, LocalDate defaultDate) throws SQLException {
        java.sql.Date d = rs.getDate(columLabel);
        return d == null ? defaultDate : d.toLocalDate();
    }

    /**
     * 获取Time
     * 
     * @param rs
     *            ResultSet
     * @param index
     *            列索引
     * @param defaultTime
     *            默认时间
     * @return 时间
     * @throws SQLException
     *             sql异常
     */
    public static LocalTime getLocalTime(ResultSet rs, int index, LocalTime defaultTime) throws SQLException {
        java.sql.Time d = rs.getTime(index);
        return d == null ? defaultTime : d.toLocalTime();
    }

    /**
     * 获取Time
     * 
     * @param rs
     *            ResultSet
     * @param columLabel
     *            列名称
     * @param defaultTime
     *            默认时间
     * @return 时间
     * @throws SQLException
     *             sql异常
     */
    public static LocalTime getLocalTime(ResultSet rs, String columLabel, LocalTime defaultTime) throws SQLException {
        java.sql.Time d = rs.getTime(columLabel);
        return d == null ? defaultTime : d.toLocalTime();
    }
    /**
     * 获取YearMonth
     * @param rs
     * @param index
     * @param m
     * @return
     * @throws SQLException
     * @since 2019-10-29
     */
    public static YearMonth getYearMonth(ResultSet rs, int index, YearMonth m) throws SQLException{
        String d = rs.getString(index);
        return d == null ? m : YearMonth.parse(d);
    }
    /**
     * 获取YearMonth
     * @param rs
     * @param columLabel
     * @param m
     * @return
     * @throws SQLException
     * @since 2019-10-29
     */
    public static YearMonth getYearMonth(ResultSet rs, String columLabel, YearMonth m) throws SQLException{
        String d = rs.getString(columLabel);
        return d == null ? m : YearMonth.parse(d);
    }
}
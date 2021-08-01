package com.cnscud.xpower.utils;

import static java.time.ZoneId.systemDefault;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 * 时间日期相关的Utils.
 * 
 * @author Felix Zhang Date 2013-02-26 21:59
 * @version 1.0.0
 */
public class DateTimeUtils {

    public static final String DEFAULT_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    /** yyyy-MM-dd HH:mm:ss */
    public static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_FORMATTER);
    /** yyyy-MM-dd */
    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER =  DateTimeFormatter.ofPattern("yyyy-MM-dd");
    /** yyyy-M-d */
    public static final DateTimeFormatter SHORT_DATE_FORMATTER =  DateTimeFormatter.ofPattern("yyyy-M-d");
    /** yyyy/MM/dd */
    public static final DateTimeFormatter SLASH_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    //
    private static final long SMALL_DATE_MILLS = toMillis(LocalDate.of(2000, 1, 1));//快速解析long时间的范围
    private static final long LARGE_DATE_MILLS = toMillis(LocalDate.of(2038, 1, 1));//快速解析long时间的范围
    /**
     * 现在时间，格式：yyyy-MM-dd HH:mm:ss
     * 
     * @return 当前时间
     * @since 2014年11月27日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static String now() {
        return now(DEFAULT_FORMATTER);
    }

    /**
     * 解析日期时间格式
     * 
     * @param source
     *            字符串日期，支持 yyyy-MM-dd HH:mm:ss 和 yyyy-MM-ddTHH:mm:ss(首选)
     * @return 日期时间
     * @throws NullPointerException
     *             如果source为空
     * @throws DateTimeParseException
     *             if the text cannot be parsed
     * @since 2015年4月22日
     * @author Ady Liu (imxylz@gmail.com)
     * @see #toLocalDateTime(String)
     */
    public static LocalDateTime parse(String source) {
        return toLocalDateTime(source);
    }

    /**
     * 现在时间
     * 
     * @param pattern
     *            输出格式
     * @return 当前时间
     * @since 2014年11月27日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static String now(String pattern) {
        return dateToString(new Date(), pattern);
    }

    /**
     * 格式化输出 "yyyy-MM-dd HH:mm:ss"
     */
    public static String toString(Date t) {
        return dateToString(t, DEFAULT_FORMATTER);
    }

    /** 格式化输出 "yyyy-MM-dd HH:mm:ss" */
    public static String toString(LocalDateTime t) {
        return t == null ? null : t.format(DEFAULT_DATETIME_FORMATTER);
    }

    /** 格式化输出 "yyyy-MM-dd HH:mm:ss" */
    public static String toString(long t) {
        return toString(new Date(t));
    }
    /** 格式化输出 "yyyy-MM-dd" */
    public static String toString(LocalDate t) {
        return t == null ? null : t.format(DEFAULT_DATE_FORMATTER);
    }
    /**格式化时长，以下输出：3时， 3时1分2秒， 3时2分*/
    public static String toChineseString(Duration d) {
        return d == null ? null: d.withNanos(0).toString().replace("PT", "").replace('H', '时').replace('M', '分').replace('S', '秒');
    }
    /**格式化时长，以下输出：3时， 3时1分2秒， 3时2分*/
    public static String toChineseString(Duration d, boolean keepSecond) {
        if(d != null && !keepSecond) {
            d = Duration.ofSeconds(d.getSeconds()/60*60);
        }
        return toChineseString(d);
    }
    /**格式化星期，输出 "星期五" 或者 "周五" */
    public static String toString(DayOfWeek dow, String prefix) {
        String week = dow == DayOfWeek.MONDAY ? "一": dow == DayOfWeek.TUESDAY ? "二"//
                        : dow == DayOfWeek.WEDNESDAY ? "三": dow == DayOfWeek.THURSDAY ? "四"//
                        : dow == DayOfWeek.FRIDAY ? "五": dow == DayOfWeek.SATURDAY ? "六" : "日";
        return (prefix == null ? "星期" : prefix)+week;
    }
    public static String dateToString(Date src, String pattern) {
        return dateToString(src, pattern, null);
    }

    public static String dateToString(Date src, String pattern, String defaultValue) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            return format.format(src);
        } catch (Exception e) {
            // do nothing
        }
        return defaultValue;
    }

    public static Date stringToDate(String src, String pattern) {
        return stringToDate(src, pattern, null);
    }

    public static Date stringToDate(String src, String pattern, Date defaultDate) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);

            return format.parse(src);
        } catch (Exception e) {
            // do nothing
        }
        return defaultDate;
    }

    public static String longToInterval(long l) {
        try {
            final long hr = TimeUnit.MILLISECONDS.toHours(l);
            final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
            final long sec = TimeUnit.MILLISECONDS.toSeconds(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min));
            return String.format("%02d:%02d:%02d", hr, min, sec);
        } catch (Exception e) {
            // do nothing
        }
        return null;
    }

    /**
     * 获取与今天相比之前或者之后的某天的开始时间 正数为之后，负数为之前
     * 
     * @param days
     * @return
     */
    public static Date getDaysStart(int days) {
        Date date = DateUtils.addDays(new Date(), days);
        return getDayStart(date);
    }

    /**
     * 获取某一天的开始时间 ex:2017-11-12 03:04:05 return 2017-11-12 00:00:00
     * 
     * @param date
     * @return
     */
    public static Date getDayStart(Date date) {
        return DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
    }

    /**
     * 计算两个日期相差几个自然天，例如：2014-12-31 20:00 至 2015-01-01 07:00 相差-1天
     * 
     * @param c1
     *            第一个日期（大）
     * @param c2
     *            第二个日期（小）
     * @return 相差天数，如果第一个日期小则为负数或者0
     * @author Ady Liu (imxylz@gmail.com)
     * @since 2014年12月4日
     */
    public static int calNatureDay(Calendar c1, Calendar c2) {
        final long day = TimeUnit.DAYS.toMillis(1);
        c1.set(c1.get(YEAR), c1.get(MONTH), c1.get(DAY_OF_MONTH), 0, 0, 0);
        c2.set(c2.get(YEAR), c2.get(MONTH), c2.get(DAY_OF_MONTH), 0, 0, 0);
        return (int) ((c1.getTimeInMillis() - c2.getTimeInMillis()) / day);
    }

    /**
     * 计算两个日期相差几个自然天，例如：2014-12-31 20:00 至 2015-01-01 07:00 相差-1天
     * 
     * @param t1
     *            第一个日期（大）,毫秒或者unix秒
     * @param t2
     *            第二个日期（小），毫秒或者unix秒
     * @return 相差天数，如果第一个日期小则为负数或者0
     * @since 2014年12月4日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static int calNatureDay(long t1, long t2) {
        t1 = t1 < Integer.MAX_VALUE ? t1 * 1000L : t1;
        t2 = t2 < Integer.MAX_VALUE ? t2 * 1000L : t2;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTimeInMillis(t1);
        c2.setTimeInMillis(t2);
        return calNatureDay(c1, c2);
    }

    /**
     * 计算两个日期相差几个自然天，例如：2014-12-31 20:00 至 2015-01-01 07:00 相差-1天
     * 
     * @param t1
     *            第一个日期（大）
     * @param t2
     *            第二个日期（小）
     * @return 相差天数，如果第一个日期小则为负数或者0
     * @since 2015年1月14日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static int calNatureDay(LocalDateTime t1, LocalDateTime t2) {
        t1 = t1.truncatedTo(ChronoUnit.DAYS);
        t2 = t2.truncatedTo(ChronoUnit.DAYS);
        return (int) Duration.between(t2, t1).toDays();
    }

    /**
     * convert LocalDateTime to milli seconds
     * 
     * @param t
     *            LocalDateTime
     * @return 0 or milli seconds
     * @since 2016年1月20日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static long toMillis(LocalDateTime t) {
        return t == null ? 0L : t.atZone(systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * convert LocalDate to milli seconds
     * 
     * @param t
     *            LocalDate
     * @return 0 or milli seconds
     * @since 2016年1月20日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static long toMillis(LocalDate t) {
        return t == null ? 0L : t.atStartOfDay().atZone(systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * convert milli seconds to LocalDateTime
     * 
     * @param epochMilli
     *            milli seconds
     * @return LocalDateTime
     * @since 2016年1月20日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static LocalDateTime toLocalDateTime(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), systemDefault());
    }
    /**
     * convert date to LocalDateTime
     * @param date old date format
     * @return LocalDateTime or null
     * @since 2016年3月9日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date == null ? null : toLocalDateTime(date.getTime());
    }
    /**
     * 将字符串转换为日期时间 yyyy-MM-dd HH:mm:ss 和 yyyy-MM-ddTHH:mm:ss
     * <ol>
     * <li>yyyy-MM-dd HH:mm:ss</li>
     * <li>yyyy-MM-ddTHH:mm:ss</li>
     * <li>1462843919831</li>
     * <li>1462843919</li>
     * </ol>
     * @param text 字符串格式
     * @return LocalDateTime
     * @since 2016年4月29日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static LocalDateTime toLocalDateTime(String text) {
        if(text == null || text.length() == 0) {
            return null;
        }
        long t = _parseFastDatetime(text);
        if (t > 0) {
            return toLocalDateTime(t);
        }
        try {
            return LocalDateTime.parse(text);
        } catch (DateTimeParseException dtpe) {
            try {
                return LocalDateTime.parse(text, DEFAULT_DATETIME_FORMATTER);
            } catch (DateTimeException dtpe2) {
                return null;
            }
        }
    }
    /**
     * convert milli seconds to LocalDate
     * 
     * @param epochMilli
     *            milli seconds
     * @return LocalDate
     * @since 2016年1月20日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static LocalDate toLocalDate(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), systemDefault()).toLocalDate();
    }
    /**
     * convert date to LocalDate
     * @param date old date format
     * @return LocalDate or null
     * @since 2016年3月9日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static LocalDate toLocalDate(Date date) {
        return date == null ? null : toLocalDate(date.getTime());
    }
    /**将时间转换成分钟，范围 0-1439之间。*/
    public static int toMinute(LocalTime time) {
        return time.getHour() * 60 + time.getMinute();
    }
    /**
     * convert string to LocalDate
     * <ol>
     * <li>2016-05-10</li>
     * <li>2016-5-10</li>
     * <li>2016/06/10</li>
     * <li>1462843919831</li>
     * <li>1462843919</li>
     * </ol>
     * @param text string format, both support yyyy-MM-dd, yyyy-M-d, unix seconds, unix milliseconds
     * @return LocalDate
     * @since 2016年4月29日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static LocalDate toLocalDate(String text) {
        if(text == null || text.length() == 0) {
            return null;
        }
        long t = _parseFastDatetime(text);
        if (t > 0) {
            return toLocalDate(t);
        }
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException dtpe) {
            try {
                return LocalDate.parse(text, SHORT_DATE_FORMATTER);
            } catch (DateTimeException dtpe2) {
                return LocalDate.parse(text, SLASH_DATE_FORMATTER);
            }
        }
    }
    /**
     * convert string to LocalDate
     * @see #toLocalDate(String)
     */
    public static LocalDate toLocalDate(String text,LocalDate defaultValue) {
        try {
            return toLocalDate(text);
        }catch(Exception ex) {
            return defaultValue;
        }
    }

    static List<DateTimeFormatter> yearMonthFormatters = Arrays.asList(//
            DateTimeFormatter.ofPattern("yyyy-MM"), //
            DateTimeFormatter.ofPattern("yyyyMM")//
    );
    /**
     * 解析 YearMonth('yyyy-MM', 'yyyyMM')，例如 2017-01 或者 201701
     * @param text 文本
     * @param defaultValue 解析失败，返回默认值
     * @return 年月
     * @author Ady Liu (imxylz@gmail.com)
     * @since 2017年1月9日
     */
    public static YearMonth toYearMonth(String text, YearMonth defaultValue) {
        for (DateTimeFormatter f : yearMonthFormatters) {
            try {
                return YearMonth.parse(text, f);
            } catch (Exception e) {
                // ingore
            }
        }
        return defaultValue;
    }
    
    /** 解析合法的时间，失败返回 -1 */
    private static long _parseFastDatetime(String text) {
        try {
            long v = Long.parseLong(text);
            v = v < SMALL_DATE_MILLS ? v * 1000L : v;
            return v >= SMALL_DATE_MILLS && v <= LARGE_DATE_MILLS ? v : -1;
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }
    
    private static final int MAX_DAY_MONTH = 31;
    private static final int MAX_DAY_YEAR = 31 * 12;
    /**将day（1~12*31~24*31）范围内的day转换成LocalDate
     * <pre>
        LocalDate now = LocalDate.of(2016, 10, 13);
        assertEquals(LocalDate.of(2016, 1, 1), DateTimeUtils.toLocalDate(now, 1));//1-1
        assertEquals(LocalDate.of(2016, 1, 31), DateTimeUtils.toLocalDate(now, 31));//1-31
        assertEquals(LocalDate.of(2016, 2, 1), DateTimeUtils.toLocalDate(now, 31+1));//2-1
        assertEquals(LocalDate.of(2016, 2, 29), DateTimeUtils.toLocalDate(now, 31+29));//2-29
        assertNull(DateTimeUtils.toLocalDate(now, 31+30));//2-30
        assertNull(DateTimeUtils.toLocalDate(now, 31+31));//2-31
        
        assertEquals(LocalDate.of(2016, 12, 31), DateTimeUtils.toLocalDate(now, 372));
        assertEquals(LocalDate.of(2017, 1, 1), DateTimeUtils.toLocalDate(now, 372+1));
        assertEquals(LocalDate.of(2017, 1, 31), DateTimeUtils.toLocalDate(now, 372+31));
        assertEquals(LocalDate.of(2017, 2, 28), DateTimeUtils.toLocalDate(now, 372+31+28));
        assertNull(DateTimeUtils.toLocalDate(now, 372+31+29));//2017-2-29 无效,非闰年
        assertNull(DateTimeUtils.toLocalDate(now, 372+31+31));//2017-2-31 无效
        </pre>
     * @param now 以某个时间段为参考点（若x对应的 month和day 在此之前则认为是来年，year+1。例如今天是2016-10-13，若x对应09-30则返回2017-09-30）
     * @param x 1~743范围内整数
     * @return 具体某个日期或者null（例如62，对应着2月31日，不存在，所以返回null）
     * @since 2016年10月13日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static LocalDate toLocalDate(LocalDate now, int x) {
        // x:1-31 -> month=1 -> day=x
        // x:32-62 -> month=2 -> day=x-31 -> day=x-(month-1)*31
        // x:63-93 -> month=3 -> day=x-62 -> day=x-(month-1)*31
        // x:94-124 -> month=4 -> day=x-93 -> day=x-(month-1)*31
        int month = (x - 1) / MAX_DAY_MONTH + 1; // 月份值 1-12
        int day = x - (month - 1) * MAX_DAY_MONTH; // 天的值 1-31
        LocalDate n1 = x > MAX_DAY_YEAR ? now.plusYears(1).withMonth(month - 12) : now.withMonth(month);
        n1 = day > n1.lengthOfMonth() ? null : n1.withDayOfMonth(day);
        return n1;
    }
    /**
     * 特殊用法，获取某天在一年中的位置（固定从1开始，每个月31天），例如1月1日=1，1月31日=31，2月31日=62等等
     * @param now 具体日期
     * @return 一年中的位置
     * @since 2016年10月13日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static int toDayWithMax(LocalDate now) {
        return (now.getMonthValue() -1 ) * 31 + now.getDayOfMonth();
    }
    
    /**
     * 将多个排好序的dates连接起来
     * <pre>例如 
         2016-10-13,2016-12-30,2016-12-31,2017-01-01,2017-02-01 
     转换成
         [2016-10-13~2016-10-13, 2016-12-30~2017-01-01, 2017-02-01~2017-02-01]
         </pre>
     * @param dates 排好序的间断日期
     * @return 排好序的日期段，含起始日期
     * @since 2016年10月13日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static List<Tuple<LocalDate, LocalDate>> toTuples(Iterable<LocalDate> dates){
        List<Tuple<LocalDate, LocalDate>> ret = new ArrayList<>();
        Tuple<LocalDate,LocalDate> next = null;
        for (LocalDate x : dates) {
            if (next == null || !next.end.plusDays(1).equals(x)) {
                next = new Tuple<>(x, x);
                ret.add(next);
            } else {
                next.end = x;
            }
        }
        return ret;
    }

    public static Date getGoodSendDateTime(Date date, int expectHour) {
        long hour = DateUtils.getFragmentInHours(date, Calendar.DAY_OF_YEAR);

        //8点0分到15分之间发出
        int minute = RandomUtils.nextInt(15);
        date = DateUtils.setHours(date, expectHour);
        date = DateUtils.setMinutes(date, minute);

        if (hour >= 0 && hour < expectHour) {
            return date;
        }

        return null;
    }

    /**
     * 获得日期 n 天以前
     * @param dateStr
     * @param days
     * @param fmt
     * @return
     */
    public static String getDayAgo(String dateStr, int days, String fmt) {
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        try {
            Date date = sdf.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, -days);
            Date dateAgo = cal.getTime();
            return sdf.format(dateAgo);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 转换日期格式
     * @param t 日期时间
     * @param defaultIfNull 如果t为null时的默认值
     * @return 老版本日期时间
     * @see #toLocalDateTime(Date)
     * @since 2017年12月5日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static Date toDate(LocalDateTime t, Date defaultIfNull) {
        return t == null ? defaultIfNull : new Date(toMillis(t));
    }
    
    /**
     * 转换日期格式
     * @param t 日期
     * @param defaultIfNull 如果t为null时的默认值
     * @return 老版本日期时间
     * @see #toLocalDate(Date)
     * @since 2017年12月5日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static Date toDate(LocalDate t, Date defaultIfNull) {
        return t == null ? defaultIfNull : new Date(toMillis(t));
    }
    /**
     * 转换日期格式
     * @param t 日期
     * @return 老版本日期时间
     * @see #toDate(LocalDate, Date)
     * @since 2017年12月5日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static Date toDate(LocalDate t) {
        return toDate(t, null);
    }
    /**
     * 转换日期格式
     * @param t 日期时间
     * @return 老版本日期时间
     * @see #toDate(LocalDateTime, Date)
     * @since 2017年12月5日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static Date toDate(LocalDateTime t) {
        return toDate(t, null);
    }
}

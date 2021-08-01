package com.cnscud.xpower.dao;

import static java.lang.String.format;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * sql的where条件构造器
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2017年3月14日
 */
public class SqlWhere {
    
    public static interface Paramable<V>{
        V call();
    }
    
    final List<Object> params = new ArrayList<>();
    final StringBuilder sql = new StringBuilder(128);

    public SqlWhere() {
    }

    public SqlWhere(CharSequence prefix, Object... args) {
        sql.append(prefix);
        for (Object arg : args) {
            params.add(arg);
        }
    }

    public SqlWhere orderBy(final String orderBy, final boolean asc) {
        return append(orderBy != null, format(" order by %s %s", orderBy, asc ? "ASC" : "DESC"));
    }
    /**
     * 最后添加分页参数
     * @param page 从1开始
     * @param size 最小是1
     * @return 添加分页参数
     */
    public SqlWhere page(int page, int size) {
        page = Math.max(1, page);
        size = Math.max(1, size);
        sql.append(format(" limit %s,%s", (page - 1) * size, size));
        return this;
    }
    /**
     * 最后添加分页参数
     * @param offset 起始偏移量，从0开始
     * @param limit 分页大小，最小为1
     * @return 添加分页参数
     */
    public SqlWhere offset(int offset, int limit) {
        sql.append(format(" limit %s,%s", offset,limit));
        return this;
    }
    /**
     * 最后添加分页参数
     * @param offset 起始偏移量，从0开始
     * @param limit 分页大小，最小为1
     * @return 添加分页参数
     */
    public SqlWhere offset(long offset, long limit) {
        sql.append(format(" limit %s,%s", offset,limit));
        return this;
    }
    
    public SqlWhere append(CharSequence appendSql, Object... args) {
        sql.append(appendSql);
        for (Object arg : args) {
            params.add(arg);
        }
        return this;
    }

    public SqlWhere append(CharSequence appendSql, Collection<?> args) {
        sql.append(appendSql);
        if (args != null) {
            params.addAll(args);
        }
        return this;
    }
    public SqlWhere append(boolean checkTrue, CharSequence appendSql, Object... args) {
        return checkTrue ? append(appendSql, args) : this;
    }

    public SqlWhere append(boolean checkTrue, CharSequence appendSql, Collection<?> args) {
        return checkTrue ? append(appendSql, args) : this;
    }
    public SqlWhere join(String table, String onConditon, Object... args) {
        return append(format(" JOIN %s on %s", table, onConditon), args);
    }

    public SqlWhere with(String fieldName, String value) {
        return andEquals(value != null && value.length() > 0, fieldName, value);
    }

    public SqlWhere withLike(String fieldName, String value) {
        return like(value != null && value.length() > 0, fieldName, value);
    }

    public SqlWhere like(boolean checkTrue, String fieldName, String value) {
        if (checkTrue) {
            sql.append(format(" and %s like ?", fieldName));
            params.add('%' + value + '%');
        }
        return this;
    }

    public SqlWhere orLike(boolean checkTrue, String fieldName, String value) {
        if (checkTrue) {
            sql.append(format(" or %s like ?", fieldName));
            params.add('%' + value + '%');
        }
        return this;
    }
    public SqlWhere andNotEquals(String fieldName, Object value) {
        return andNotEquals(true, fieldName, value);
    }

    public SqlWhere andNotEquals(boolean checkTrue, String fieldName, Object value) {
        if (checkTrue) {
            sql.append(format(" and %s<>?", fieldName));
            params.add(value);
        }
        return this;
    }
    public <T> SqlWhere andNotEquals(boolean checkTrue, String fieldName, Paramable<T> f) {
        if (checkTrue) {
            sql.append(format(" and %s<>?", fieldName));
            params.add(f.call());
        }
        return this;
    }
    public SqlWhere andEquals(String fieldName, Object value) {
        return andEquals(true, fieldName, value);
    }
    public SqlWhere andEquals(boolean checkTrue, String fieldName, Object value) {
        if (checkTrue) {
            sql.append(format(" and %s=?", fieldName));
            params.add(value);
        }
        return this;
    }
    public <T> SqlWhere andEquals(boolean checkTrue, String fieldName, Paramable<T> f) {
        if (checkTrue) {
            sql.append(format(" and %s=?", fieldName));
            params.add(f.call());
        }
        return this;
    }
    public SqlWhere orEquals(String fieldName, Object value) {
        return orEquals(true, fieldName, value);
    }
    public SqlWhere orEquals(boolean checkTrue, String fieldName, Object value) {
        if (checkTrue) {
            sql.append(format(" or %s=?", fieldName));
            params.add(value);
        }
        return this;
    }
    public SqlWhere and(String subq, Object... args) {
        return and(true, subq, args);
    }
    /**
     * and语句
     * 
     * @param checkTrue
     *            cond.getCreatedAtEnd() > 0, -- 条件
     * @param subq
     *            "created_at<=?", -- sql语句
     * @param args
     *            new Timestamp(cond.getCreatedAtEnd()) -- 对象值
     * @return 对象
     */
    public SqlWhere and(boolean checkTrue, String subq, Object... args) {
        if (checkTrue) {
            sql.append(" and ").append(subq);
            for (Object arg : args) {
                params.add(arg);
            }
        }
        return this;
    }
    public SqlWhere or(String subq, Object... args) {
        return or(true, subq, args);
    }
    public SqlWhere or(boolean checkTrue, String subq, Object... args) {
        if (checkTrue) {
            sql.append(" or ").append(subq);
            for (Object arg : args) {
                params.add(arg);
            }
        }
        return this;
    }

    public SqlWhere andIn(String fieldName, Collection<?> args) {
        if (args != null && args.size() > 0) {
            sql.append(format(" and %s in (%s)", fieldName, SqlUtils.buildQuestionMark(args.size())));
            params.addAll(args);
        }
        return this;
    }

    public SqlWhere orIn(String fieldName, Collection<?> args) {
        if (args != null && args.size() > 0) {
            sql.append(format(" or %s in (%s)", fieldName, SqlUtils.buildQuestionMark(args.size())));
            params.addAll(args);
        }
        return this;
    }

    public SqlWhere andBetween(String fieldName, Object from, Object end) {
        if (from != null && end != null) {
            sql.append(format(" and %s between ? and ?", fieldName));
            params.add(from);
            params.add(end);
        }
        return this;
    }
    public SqlWhere andIncludeEndBetween(String fieldName, LocalDate from, LocalDate includeEndOfDay) {
        if(from == null && includeEndOfDay == null) {
            return this;
        }
        from = from == null ? LocalDate.of(1970, 1, 1) : from;
        LocalDate to = includeEndOfDay == null ? LocalDate.of(2038, 12, 31) : includeEndOfDay.plusDays(1);
        sql.append(format(" and %s between ? and ?", fieldName));
        params.add(from);
        params.add(to);
        return this;
    }

    public SqlWhere orBetween(String fieldName, Object from, Object end) {
        if (from != null && end != null) {
            sql.append(format(" or %s between ? and ?", fieldName));
            params.add(from);
            params.add(end);
        }
        return this;
    }

    public StringBuilder getSql() {
        return sql;
    }

    public List<Object> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return sql.toString();
    }

    public static void main(String[] args) throws Exception {
        SqlWhere where = new SqlWhere("select * from users where 1");
        where.and(true, "uid>?", 10)//
                .and(true, "created_at between ? and ?", LocalDateTime.now(), LocalDateTime.now().plusDays(1))//
                .or(10 > 0, "age=?", 10)//
                .andIn("country", Arrays.asList("德国", "美国"))//
                .andBetween("num", 1, 10)
                .with("nick", "Ady Liu")//
                .append(" order by uid desc").page(1, 50);
        System.out.println(where.toString());
        System.out.println(where.params);
    }
}

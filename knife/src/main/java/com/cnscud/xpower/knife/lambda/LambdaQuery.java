package com.cnscud.xpower.knife.lambda;

import com.cnscud.xpower.knife.IFunction;
import com.cnscud.xpower.dao.SqlUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author mie 2021-02-22 09:49
 * @version 1.0.0
 */
public class LambdaQuery<T> extends Wrapper {

    protected final StringBuilder sb = new StringBuilder();
    protected final List<Object> params = new LinkedList<>();


    /**
     * not in
     *
     * @param function
     * @param v
     * @return
     */
    public LambdaQuery<T> notIn(IFunction<T, ?> function, Collection<? extends Serializable> v) {
        String column = resolve(function);
        sb.append(" and ").append(column).append(String.format(" not in (%s)", SqlUtils.buildQuestionMark(v.size())));
        params.addAll(v);
        return this;
    }

    /**
     * in
     *
     * @param function
     * @param v
     * @return
     */
    public LambdaQuery<T> in(IFunction<T, ?> function, Collection<? extends Serializable> v) {
        String column = resolve(function);
        sb.append(" and ").append(column).append(String.format(" in (%s)", SqlUtils.buildQuestionMark(v.size())));
        params.addAll(v);
        return this;
    }


    /**
     * equal to 等于
     *
     * @param function
     * @param v
     * @return
     */
    public LambdaQuery<T> eq(IFunction<T, ?> function, Object v) {
        String column = resolve(function);
        sb.append(" and ").append(column).append(" =? ");
        params.add(v);
        return this;
    }

    /**
     * @param function
     * @param v
     * @return
     */
    public LambdaQuery<T> or(IFunction<T, ?> function, Object v) {
        String column = resolve(function);
        sb.append(" or ").append(column).append(" =? ");
        params.add(v);
        return this;
    }


    /**
     * not equal 不等于
     *
     * @param function
     * @param v
     * @return
     */
    public LambdaQuery<T> ne(IFunction<T, ?> function, Object v) {
        sb.append(" and ").append(resolve(function)).append(" !=? ");
        params.add(v);
        return this;
    }

    /**
     * greater than 大于
     *
     * @param function
     * @param v
     * @return
     */
    public LambdaQuery<T> gt(IFunction<T, ?> function, Object v) {
        sb.append(" and ").append(resolve(function)).append(" >? ");
        params.add(v);
        return this;
    }

    /**
     * greater than or equal to 大于等于
     *
     * @param function
     * @param v
     * @return
     */
    public LambdaQuery<T> ge(IFunction<T, ?> function, Object v) {
        sb.append(" and ").append(resolve(function)).append(" >=? ");
        params.add(v);
        return this;
    }

    /**
     * less than 小于
     *
     * @param function
     * @param v
     * @return
     */
    public LambdaQuery<T> lt(IFunction<T, ?> function, Object v) {
        sb.append(" and ").append(resolve(function)).append(" <? ");
        params.add(v);
        return this;
    }

    /**
     * less than or equal to 小于等于
     *
     * @param function
     * @param v
     * @return
     */
    public LambdaQuery<T> le(IFunction<T, ?> function, Object v) {
        sb.append(" and ").append(resolve(function)).append(" <=? ");
        params.add(v);
        return this;
    }

    public LambdaQuery<T> between(IFunction<T, ?> function, Object v1, Object v2) {
        sb.append(" and ").append(resolve(function)).append(" between ? and ?");
        params.add(v1);
        params.add(v2);
        return this;
    }

    public LambdaQuery<T> nonBetween(IFunction<T, ?> function, Object v1, Object v2) {
        sb.append(" and ").append(resolve(function)).append(" not between ? and ?");
        params.add(v1);
        params.add(v2);
        return this;
    }

    public LambdaQuery<T> like(IFunction<T, ?> function, Object v) {
        sb.append(" and ").append(resolve(function)).append(" like ? ");
        params.add("%" + v + "%");
        return this;
    }

    public LambdaQuery<T> likeLeft(IFunction<T, ?> function, Object v) {
        sb.append(" and ").append(resolve(function)).append(" like ? ");
        params.add("%" + v);
        return this;
    }

    public LambdaQuery<T> likeRight(IFunction<T, ?> function, Object v) {
        sb.append(" and ").append(resolve(function)).append(" like ? ");
        params.add(v + "%");
        return this;
    }

    public LambdaQuery<T> nonLike(IFunction<T, ?> function, Object v) {
        sb.append(" and ").append(resolve(function)).append(" not like ? ");
        params.add("%" + v + "%");
        return this;
    }

    public LambdaQuery<T> orderByDesc(IFunction<T, ?> function) {
        sb.append(" order by ").append(resolve(function)).append(" desc ");
        return this;
    }


    /** 最后拼接sql 注意防止sql注入 */
    public LambdaQuery<T> lastSql(String sql){
        sb.append(sql);
        return this;
    }


}

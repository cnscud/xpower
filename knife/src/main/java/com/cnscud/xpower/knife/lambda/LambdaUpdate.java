package com.cnscud.xpower.knife.lambda;

import com.cnscud.xpower.knife.IFunction;
import com.cnscud.xpower.dao.SqlUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author mie peng 2021-02-24 13:11
 * @version 1.0.0
 */
public class LambdaUpdate<T> extends Wrapper {

    protected final StringBuilder sb = new StringBuilder();
    protected final List<Object> params = new LinkedList<>();
    private final AtomicBoolean first = new AtomicBoolean(true);
    private final AtomicBoolean firstWhere = new AtomicBoolean(true);

    /**
     * 判断set有没有执行过 是否需要update
     * @return
     */
    public boolean isLambdaUpdate(){
        return params.size() > 0;
    }


    /**
     * @param function
     * @param v
     * @return
     */
    public LambdaUpdate<T> set(IFunction<T, ?> function, Object v) {
        String column = resolve(function);
        if (!first.get()) {
            sb.append(",");
        }
        sb.append(column).append(" =? ");
        first.set(false);
        params.add(v);
        return this;
    }

    /**
     * eq 之前先set
     * equal to 等于
     * @param function
     * @param v
     * @return
     */
    public LambdaUpdate<T> eq(IFunction<T, ?> function, Object v) {
        if (params.size() <=0){
            throw new RuntimeException("lambda update 不能没有set值");
        }
        if (firstWhere.get()){
            sb.append(" where 1");
            firstWhere.set(false);
        }
        String column = resolve(function);
        sb.append(" and ").append(column).append(" =? ");
        params.add(v);
        return this;
    }


    /**
     * ne 之前先set
     * equal to 等于
     * @param function
     * @param v
     * @return
     */
    public LambdaUpdate<T> ne(IFunction<T, ?> function, Object v) {
        if (params.size() <=0){
            throw new RuntimeException("lambda update 不能没有set值");
        }
        if (firstWhere.get()){
            sb.append(" where 1");
            firstWhere.set(false);
        }
        String column = resolve(function);
        sb.append(" and ").append(column).append(" !=? ");
        params.add(v);
        return this;
    }


    /**
     * in 之前先set
     * @param function
     * @param vs
     * @return
     */
    public LambdaUpdate<T> in(IFunction<T,?> function, Collection<? extends Serializable> vs){
        if (params.size() <=0){
            throw new RuntimeException("lambda update 不能没有set值");
        }
        if (firstWhere.get()){
            sb.append(" where 1");
            firstWhere.set(false);
        }
        String column = resolve(function);
        sb.append(" and ").append(column).append(" in (").append(SqlUtils.buildQuestionMark(vs.size())).append(")");
        params.add(vs);
        return this;
    }
}

package com.cnscud.xpower.knife;

import com.cnscud.xpower.knife.impl.KnifeImpl;
import com.cnscud.xpower.knife.impl.TableCache;
import com.cnscud.xpower.knife.lambda.LambdaService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author mie Peng 2020-09-07 12:10
 * @version 1.0.0
 */
public interface IService<T> {
    default T queryById(Serializable id) {
        return getKnife().queryById(getBizName(), getEntity(), id);
    }

    default T queryUniq(T model) {
        return getKnife().queryUniq(getBizName(), model);
    }

    default List<T> queryUniqs(T model) {
        return getKnife().queryUniqs(getBizName(), model);
    }

    default Long insert(T model) {
        return getKnife().insert(getBizName(), model);
    }

    default int insertBatch(Collection<T> model){
        return getKnife().insertBatch(getBizName(), model);
    }

    default List<T> queryByIds(Collection<? extends Serializable> ids) {
        return getKnife().queryByIds(getBizName(), getEntity(), ids);
    }

    default Long count(T model){
        return getKnife().count(getBizName(), model);
    }

    default Long count(){
        return getKnife().count(getBizName(),  TableCache.get(getEntity()).getTableName());
    }

    default int updateById(Object model) {
        return getKnife().updateById(getBizName(), model);
    }

    default int deleteById(Object model) {
        return getKnife().deleteById(getBizName(), model);
    }

    default IPage<T> list(T model, String orderBy, boolean asc, long page, long pageSize) {
        return getKnife().list(getBizName(), model, orderBy, asc, page, pageSize);
    }
    default List<T> list(T model){
        return getKnife().list(getBizName(), model);
    }

    default List<T> list(T model,long offset,long limit){
        return getKnife().listLimit(getBizName(), model,null,false, offset, limit);
    }

    default LambdaService lambda(){
        return getKnife().lambda(getEntity(),getBizName());
    }


    Class<T> getEntity();

    String getBizName();

    default Iknife getKnife() {
        return new KnifeImpl();
    }
}

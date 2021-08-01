package com.cnscud.xpower.knife;

import com.cnscud.xpower.knife.lambda.LambdaService;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-09-23
 */
public interface Iknife {

    <T> int deleteById(String bizName, Class<T> clazz, Serializable id);

    <T> int deleteById(String bizName, T model);

    <T> int deleteByIds(String bizName, Class<T> clazz, Collection<Serializable> ids);

    <T> int deleteByMap(String bizName, Class<T> clazz, Map<String, Object> conditions);

    <T> int insertBatch(String bizName ,Collection<T> model);

    <T> Long insert(String bizName, T model);

    <T> IPage<T> list(String bizName, T model, String orderBy, boolean asc, long page, long pageSize);

    <T> List<T> listLimit(String bizName, T model, String orderBy, boolean asc, long page, long pageSize);

    <T> Long count(String bizName, String tableName);

    <T> Long count(String bizName, T model);

    default <T> List<T> list(String bizName, T model){
        return list(bizName, model, null, true, 1, 100000).getList();
    }
    <T> IPage<T> listByMap(String bizName, Class<T> clazz, Map<String, Object> conditions, String orderBy, boolean asc, long page, long pageSize);

    <T> T queryById(String bizName, Class<T> clazz, Serializable id);

    <T> List<T> queryByIds(String bizName, Class<T> clazz, Collection<? extends Serializable> ids);

    <T> T queryUniq(String bizName, T model);

    <T> List<T> queryUniqs(String bizName, T model);

    <T> T queryUniqByMap(String bizName, Class<T> clazz, Map<String, Object> conditions);

    <T> int updateById(String bizName, T model);


    <T> LambdaService lambda(Class<T> clazz, String bizName);
}

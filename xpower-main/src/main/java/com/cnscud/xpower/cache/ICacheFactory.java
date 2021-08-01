package com.cnscud.xpower.cache;

/**
 * Cache创建工厂
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-11-08
 */
public interface ICacheFactory<T extends ICache> {

    /**
     * 获取一个Cache客户端 集群版
     * 
     * @param id
     *            cache名称（注意是全局唯一的，请查询相关文档）
     * @return cache客户端或者null
     */
    T getCache(String id);

    /**
     * 获取一个Cache客户端 单机版
     *
     * @param id
     *            cache名称（注意是全局唯一的，请查询相关文档）
     * @return cache客户端或者null
     */
    T getSingleCache(String id);

    /**
     * 销毁客户端，释放资源，服务终止时可以释放资源
     * 
     * @param id
     *            Cache名称
     */
    void destroyCache(String id);

}
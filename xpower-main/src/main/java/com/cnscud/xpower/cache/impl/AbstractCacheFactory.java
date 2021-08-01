package com.cnscud.xpower.cache.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cnscud.xpower.cache.ICache;
import com.cnscud.xpower.cache.ICacheFactory;
/**
 * 
 * 
 * @author adyliu(imxylz@gmail.com)
 * @since 2012-11-8
 */
public abstract class AbstractCacheFactory<T extends ICache> implements ICacheFactory<T>{

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    

    private Map<String, T> caches = new HashMap<String, T>();

    public AbstractCacheFactory() {
    }

    @Override
    public T getCache(String id) {
        T cache = caches.get(id);
        if (cache != null) {
            return cache;
        }
        synchronized (caches) {
            cache = caches.get(id);
            if (cache == null) {
                newCacheBefore(id);
                cache = innerBuildCache(id, null);
                newCacheAfter(id, cache);
                if (cache != null)
                    caches.put(id, cache);
            }
        }
        return cache;

    }


    @Override
    public T getSingleCache(String id) {
        T cache = caches.get(id);
        if (cache != null) {
            return cache;
        }
        synchronized (caches) {
            cache = caches.get(id);
            if (cache == null) {
                newCacheBefore(id);
                cache = innerBuildSingleCache(id, null);
                newCacheAfter(id, cache);
                if (cache != null)
                    caches.put(id, cache);
            }
        }
        return cache;

    }

    protected void newCacheAfter(String id, T cache) {
    }

    protected void newCacheBefore(String id) {
    }

    protected void destroyCacheAfter(String id, T cache) {
    }

    @Override
    public void destroyCache(String id) {
        T cache = caches.remove(id);
        if (cache != null) {
            try {
                cache.destroy();
            } finally {
                destroyCacheAfter(id, cache);
            }
        }
    }

    private T innerBuildCache(String id, T oldCache) {
        long timecost = System.currentTimeMillis();
        try {
            return buildCache(id, oldCache);
        } finally {
            logger.info("buildCacheClient " + id + " over, cost(ms): " + (System.currentTimeMillis() - timecost));
        }
    }

    private T innerBuildSingleCache(String id ,T oldCache){
        long timeCost = System.currentTimeMillis();
        try {
            return buildSingleCache(id, oldCache);
        } finally {
            logger.info("buildSingleCacheClient " + id + " over, cost(ms): " + (System.currentTimeMillis() - timeCost));
        }
    }

    /**
     * 生成一个Cache实例
     * 
     * @param id
     *            cache名称
     * @param oldCache
     *            旧Cache实例，有可能只是修改旧Cache的配置，这在重新生成Cache是非常有用
     * @return 实例对象
     *         <p>
     *         如果旧实例和新实例不是一个实体对象，原则上需要销毁旧实例
     *         </p>
     * @see ICache#destroy()
     */
    protected abstract T buildCache(String id, T oldCache);

    protected abstract T buildSingleCache(String id, T oldCache);

    protected void rebuildSingCache(String id){
        T oldCache = caches.get(id);
        if (oldCache == null) {
            return;// 如果客户端没有缓存变更的配置，则不进行刷新
        }

        try {
            T newCache = innerBuildSingleCache(id, oldCache);
            synchronized (caches) {
                caches.put(id, newCache);
            }
            // 此处不再销毁实例了，销毁实例放在#buildCache中进行
        } catch (Exception e) {
            logger.error("rebuldCache " + id + " fail!", e);
        }
    }

    protected void rebuildCache(String id) {
        T oldCache = caches.get(id);
        if (oldCache == null) {
            return;// 如果客户端没有缓存变更的配置，则不进行刷新
        }

        try {
            T newCache = innerBuildCache(id, oldCache);
            synchronized (caches) {
                caches.put(id, newCache);
            }
            // 此处不再销毁实例了，销毁实例放在#buildCache中进行
        } catch (Exception e) {
            logger.error("rebuldCache " + id + " fail!", e);
        }
    }

}
package com.cnscud.xpower.dao;
/**
 * 数据库操作工厂
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-6-14
 */
public class DaoFactory {

    protected static final Dao instance = new Dao();


    /**
     * 获取一个数据库操作实例.<br/> {@link IDao}是线程安全的
     * 
     * @return 数据库操作实例
     */
    public static final IDao getIDao() {
        return instance;
    }

    
}
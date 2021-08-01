package com.cnscud.xpower.ddd.impl.specification;

import com.cnscud.xpower.ddd.impl.AbstractHikariCpConnectionFactory;

import java.util.Map;


/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-7-5
 */
public class HsqldbConnectionFactory extends AbstractHikariCpConnectionFactory {

    public HsqldbConnectionFactory(String bizName, Map<String, String> args) {
        super(bizName, args);
    }

    protected String getDriverClassName() {
        return HSQLDB_DRIVER_CLASS_NAME;
    }
}

/**
 * $Id: DataSourceExtend.java 766 2011-10-26 10:25:20Z adyliu $
 * (C)2011 Sohu Inc.
 */
package com.cnscud.xpower.ddd.impl;

import java.sql.SQLException;

import javax.sql.DataSource;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-10-26
 */
public interface DataSourceExtend extends DataSource {

    /**
     */
    void close() throws SQLException;
}

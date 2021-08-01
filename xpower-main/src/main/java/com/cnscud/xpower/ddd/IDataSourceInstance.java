package com.cnscud.xpower.ddd;

import javax.sql.DataSource;

public interface IDataSourceInstance {

    String EMPTY_PATTERN = "";

    DataSource getWriter(String pattern);

    DataSource getReader(String pattern);

    long getUpdatetime();

    void close();

    String getBizName();
}

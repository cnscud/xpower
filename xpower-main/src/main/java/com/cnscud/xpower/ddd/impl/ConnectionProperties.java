package com.cnscud.xpower.ddd.impl;

/**
 * JDBC URL Connection constants
 */
public interface ConnectionProperties {

    String MIN = "min";

    String MAX = "max";

    String INIT = "init";

    String USER = "user";

    String PASSWORD = "password";

    String VALIDATIONQUERY = "validationquery";

    String URL = "url";

    String HOST = "host";

    String PORT = "port";

    String CONNECT_TIMEOUT = "connectTimeout";

    String SOCKET_TIMEOUT = "socketTimeout";

    String DATABASE = "database";

    String CHARSET = "charset";

    String MYSQL_DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";

    String HSQLDB_DRIVER_CLASS_NAME = "org.hsqldb.jdbc.JDBCDriver";

    String ORACLE_DRIVER_CLASS_NAME = "oracle.jdbc.driver.OracleDriver";
}

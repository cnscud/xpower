package com.cnscud.xpower.ddd.impl.specification;

import com.cnscud.xpower.ddd.impl.AbstractHikariCpConnectionFactory;

import java.util.Map;


/**
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-7-28
 */
public class OracleConnectionFactory extends AbstractHikariCpConnectionFactory {

    final String urlFormat = "jdbc:oracle:thin:@%s:%s:%s";

    public OracleConnectionFactory(String bizName, Map<String, String> args) {
        super(bizName, args);
    }

    protected String getDriverClassName() {
        return ORACLE_DRIVER_CLASS_NAME;
    }

    @Override
    protected String getUrl(Map<String, String> args) {
        String url = args.get(URL);
        if (url == null) {
            url = String.format(urlFormat, args.get(HOST), //
                    args.get(PORT) == null ? "1521" : args.get(PORT), //
                    args.get(DATABASE));
        }
        return url;
    }
}

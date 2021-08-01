package com.cnscud.xpower.ddd.impl.specification;

import com.cnscud.xpower.ddd.impl.AbstractHikariCpConnectionFactory;

import java.util.Map;


public class MySqlConnectionFactory extends AbstractHikariCpConnectionFactory {

    final String urlFormat = "jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=%s&autoReconnect=true&rewriteBatchedStatements=true&connectTimeout=%s&socketTimeout=%s&useSSL=false&useAffectedRows=true&&zeroDateTimeBehavior=convertToNull";

    public MySqlConnectionFactory(String bizName, Map<String, String> args) {
        super(bizName, args);
    }

    @Override
    protected String getDriverClassName() {
        return MYSQL_DRIVER_CLASS_NAME;
    }

    /*@Override
    protected Map<String, String> prepareArgs(Map<String, String> args) {
        if (!args.containsKey(VALIDATIONQUERY)) {
            args.put(VALIDATIONQUERY, "select 1");// http://mysql-qotd.casperia.net/archives/345
        }
        return super.prepareArgs(args);
    }*/

    @Override
    protected String getUrl(Map<String, String> args) {
        String url = args.get(URL);
        if (url == null) {
            url = String.format(urlFormat, //
                    args.get(HOST), getArg(args, PORT, "3306"),//
                    args.get(DATABASE), args.get(CHARSET),//
                    getArg(args, CONNECT_TIMEOUT, "5000"),// 默认连接超时5秒
                    getArg(args, SOCKET_TIMEOUT, "1800000"))// 默认socket超时30分钟
            ;
        }
        logger.debug("mysql-connect-url "+url);
        return url;
    }

}

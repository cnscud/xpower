package com.cnscud.xpower.dbn;

import com.cnscud.xpower.configcenter.ConfigCenter;
import com.cnscud.xpower.utils.Jsons;
import com.github.zkclient.IZkDataListener;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 从Zookeeper的 /xpower/dbn节点下读取数据库配置.
 * 内容支持两种格式: json或者properties格式.
 *
 * JSON格式如下:
 * {
 *   "jdbc-url": "jdbc:mysql://127.0.0.1:3306/cavedemo?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC",
 *   "username": "dbuser",
 *   "password": "yourpassword",
 *   "driver-class-name": "com.mysql.cj.jdbc.Driver"
 * }
 *
 * Properties格式如下:
 *  jdbc-url: jdbc:mysql://127.0.0.1:3306/cavedemo?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC
 *  username: dbuser
 *  password: password
 *  driver-class-name: com.mysql.cj.jdbc.Driver
 *
 * @author Felix Zhang
 * @since 2021-8-5
 */
public class SchemeNodeHelper {

    static final Logger logger = LoggerFactory.getLogger(SchemeNodeHelper.class);

    //支持两种格式: json, properties
    public static Map<String, String> getInstance(final String instanceName) throws Exception {
        String data = ConfigCenter.getInstance().getDataAsString("/xpower/dbn/" + instanceName);
        if(StringUtils.isEmpty(data)){
            return null;
        }

        data = data.trim();
        if (data.startsWith("{")) {
            //as json
            Map<String, String> swap = Jsons.fromJson(data, Map.class);
            Map<String, String> result = new HashMap<>();

            if (swap != null) {
                for (String name : swap.keySet()) {
                    result.put(name.toLowerCase(), swap.get(name));
                }
            }

            return result;
        }
        else {
            //as properties
            Properties props = new Properties();
            try {
                props.load(new StringReader(data));
            }
            catch (IOException e) {
                logger.error("loading global config failed", e);
            }

            Map<String, String> result = new HashMap<>();

            for (String name : props.stringPropertyNames()) {
                result.put(name.toLowerCase(), props.getProperty(name));
            }

            return result;
        }
    }

    public static void watchInstance(final String bizName, final IZkDataListener listener) {
        final String path = "/xpower/dbn/" + bizName;
        ConfigCenter.getInstance().subscribeDataChanges(path, listener);
    }
}

package com.cnscud.xpower.dbn;

import com.cnscud.xpower.Charsets;
import com.cnscud.xpower.configcenter.ConfigCenter;
import com.cnscud.xpower.ddd.schema.Instance;
import com.cnscud.xpower.utils.Jsons;
import com.github.zkclient.IZkDataListener;
import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TJSONProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.cnscud.xpower.ddd.schema.SchemaInstanceHelper.unserialize;

/**
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-6-13
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

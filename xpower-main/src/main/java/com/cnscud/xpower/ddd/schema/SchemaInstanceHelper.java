package com.cnscud.xpower.ddd.schema;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TJSONProtocol;

import com.github.zkclient.IZkDataListener;
import com.cnscud.xpower.Charsets;
import com.cnscud.xpower.configcenter.ConfigCenter;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2011-6-13
 */
public class SchemaInstanceHelper {

    public static String serialize(Instance instance) throws Exception {
        TSerializer ser = new TSerializer(new TJSONProtocol.Factory());
        return ser.toString(instance, Charsets.UTF8.charset);
    }

    public static Instance unserialize(String jsonData) throws Exception {
        TDeserializer des = new TDeserializer(new TJSONProtocol.Factory());
        Instance instance = new Instance();
        des.deserialize(instance, jsonData, Charsets.UTF8.charset);
        return instance;
    }

    public static Instance getInstance(final String instanceName) throws Exception {
        String data = ConfigCenter.getInstance().getDataAsString("/xpower/ddd/" + instanceName);
        return data == null ? null : unserialize(data);
    }

    public static void watchInstance(final String bizName, final IZkDataListener listener) {
        final String path = "/xpower/ddd/" + bizName;
        ConfigCenter.getInstance().subscribeDataChanges(path, listener);
    }
}

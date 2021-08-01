package com.cnscud.xpower.utils;


import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;




public class FreemarkerUtils {

    public static String getProcessResult(String model, Map map) throws IOException, TemplateException {
        Template template =  new Template(null,new StringReader(model),null);
        StringWriter out = new StringWriter();
        /* 模板数据 */
        template.process(map, out);
        out.flush();
        return out.toString();
    }
    
    public static <T> Map<String, T> processStringKeyMap(Map<Integer, T> map) {
        Map<String, T> rMap = new LinkedHashMap<String, T>();
        for (Integer key : map.keySet()) {
            rMap.put(key + "", map.get(key));
        }
        return rMap;
    }

}


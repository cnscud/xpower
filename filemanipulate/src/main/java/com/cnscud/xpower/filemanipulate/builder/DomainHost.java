package com.cnscud.xpower.filemanipulate.builder;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ftp替换的域名与host匹配
 * @author adyliu (imxylz@gmail.com)
 * @since 2016年12月22日
 */
public class DomainHost {

    String domain;
    String host;
    String ftpRootPath;
   
    DomainHost(String domain, String host, String ftpRootPath) {
        super();
        this.domain = domain;
        this.host = host;
        this.ftpRootPath = ftpRootPath;
    }
    
    public static Map<String, DomainHost> avaiable(){
        Map<String, DomainHost> m = new LinkedHashMap<>();
        m.put("f1.sjbly.cn", new DomainHost("f1.sjbly.cn", "db1", "/opt/ftpupload/f1/"));
        m.put("f2.sjbly.cn", new DomainHost("f2.sjbly.cn", "db1", "/opt/ftpupload/f2/"));
        m.put("f3.sjbly.cn", new DomainHost("f3.sjbly.cn", "star1", "/opt/ftpupload/f3/"));
        m.put("f4.sjbly.cn", new DomainHost("f4.sjbly.cn", "star2", "/opt/ftpupload/f4/"));
        m.put("f5.sjbly.cn", new DomainHost("f5.sjbly.cn", "star2", "/opt/ftpupload/f5/"));
        m.put("f6.sjbly.cn", new DomainHost("f6.sjbly.cn", "star2", "/opt/ftpupload/f6/"));
        m.put("f7.sjbly.cn", new DomainHost("f7.sjbly.cn", "cache3", "/opt/ftpupload/f7/"));
        m.put("f8.sjbly.cn", new DomainHost("f8.sjbly.cn", "cache3", "/opt/ftpupload/f8/"));
        //
        m.put("test1.sjbly.cn", new DomainHost("test1.sjbly.cn", "192.168.20.90", "/opt/ftps/data1"));
        m.put("s3.sjbly.cn", new DomainHost("s3.sjbly.cn", "192.168.20.90", "/opt/ftps/data1"));
        try {
            m.put("localhost", new DomainHost("localhost", "localhost", new File("./testfile/").getCanonicalPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return m;
    }
}

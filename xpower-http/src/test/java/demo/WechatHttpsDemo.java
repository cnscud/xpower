package demo;

import com.cnscud.xpower.http.Request;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2017年7月6日
 */
public class WechatHttpsDemo {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        Request req = new Request("https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login");
        System.out.println(req.get().asString());
    }

}

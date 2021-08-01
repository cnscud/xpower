package demo;

import com.cnscud.xpower.http.Request;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2016年4月6日
 */
public class ImDemo2 {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception{
        Request r = new Request("http://imctrl.panda.com/send_msg/guest/", "POST");
        r.addHeaders("Content-Type","application/x-www-form-urlencoded");
        r.addParams("from","4098","v","kkkkruX1/9vZ","ip","192.168.6.88","msg_info","IM测试消息@ady@20160406: "+System.currentTimeMillis());
        //
        System.out.println(r.post().asString());
    }

}

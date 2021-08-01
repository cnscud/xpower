package demo;

import com.cnscud.xpower.http.Http;
import com.cnscud.xpower.http.Request;
import com.cnscud.xpower.http.Response;

import java.io.File;
import java.util.UUID;

/**
 * @author adyliu (imxylz@gmail.com)
 * @since 2013-06-07
 */
public class HttpBinaryDemo {
    public static void main(String[] args) throws Exception {
        //http://img0.yododo.com/files/review/013F/013F0822A48D0142FF8080813F066741.jpg
        //Request req = new Request("http://www.baidu.com/search/img/logo.gif");
        String url = "http://s16.sinaimg.cn/mw690/4850e3f3gde85331cfeff";
        //String url = "http://www.baidu.com";
        Request req = new Request(url).setReferer("http://blog.sina.com.cn/baby_no_cry").setConnectionTimeout(3000).setSoTimeout(2000);

        for (int i = 0; i < 5; i++) {
            Response response = Http.execute(req);
            //写入文件
            File file = response.asFile(new File("/tmp/" + UUID.randomUUID() + ".jpg"));
            System.out.println(file+" => "+file.length());
            System.out.println(response);
        }
        //
        Http.shutdown();
    }
}

package demo;


import com.cnscud.xpower.http.Http;
import com.cnscud.xpower.http.Request;
import com.cnscud.xpower.http.Response;

/**
 * @author adyliu (imxylz@gmail.com)
 * @since 2013-06-06
 */
public class HttpDemo {
    public static void main(String[] args) throws Exception {
       // BasicConfigurator.configure();
        // send POST
        Request req;
        req = new Request("http://twitter.com").retry(3).setConnectionTimeout(1000).withHttpProxy("192.168.6.21", 3128);
        System.out.println(Http.execute(req).asString());

        Response resp = Http.execute(new Request("http://is.panda.com/user/get/").setMethod("POST").addParams("uid", "" + 1));
        System.out.println(resp.asString());

        // send GET
        resp = Http.execute(new Request("http://is.panda.com/user/get/").addParams("uid", "" + 1));
        System.out.println(resp.asString());
        //
        req = new Request("http://tmall.panda.com/api/nav/gettips").addParams("indent", "1", "poi_ids", "3454,32017");
        resp = Http.execute(req);
        // System.out.println(resp.asString());

        Http.shutdown();
    }
}

package demo;

import com.cnscud.xpower.http.Http;
import com.cnscud.xpower.http.Request;
import com.cnscud.xpower.http.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author adyliu (imxylz@gmail.com)
 * @since 2013-06-14
 */
public class HttpCookieDemo {
    public static void main(String[] args) throws Exception {
        Request req;
        req = new Request("http://www.panda.com");
        req.withCookieStore().withHttpProxy("127.0.0.1", 7000);
        Response response;
        response = Http.execute(req);
        System.out.println(Arrays.toString(response.getHeaders()));
        System.out.println("Set-Cookie => " + response.getHeaderValue("Set-Cookie"));
        // System.out.println(response.asString());
        // System.out.println(response.getCookieStore().getCookies());
        System.out.println("nsid => " + response.getCookieValue("nsid"));
        System.out.println(response);
        //
        List<Thread> threads = new ArrayList<>();
        final Map<String, Boolean> nsidMap = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            final int index = i;
            Thread t = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    try {
                        Response resp2 = new Request("http://www.panda.com/").withHttpProxy("127.0.0.1", 7000).get();
                        String nsid = resp2.getCookieValue("nsid");
                        //System.out.println(index + "," + j + " =>newnsid2=> " + nsid);
                        if(null  != nsidMap.putIfAbsent(nsid, Boolean.TRUE)) {
                            System.err.println("ERROR "+nsid);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
            threads.add(t);
        }
       for(Thread t:threads) {
           t.join();
       }
    }
}

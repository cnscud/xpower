/**
 * 
 */
package demo;

import com.cnscud.xpower.http.ExtraCnx;
import com.cnscud.xpower.http.Http;
import com.cnscud.xpower.http.Request;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2014年11月25日
 */
public class HttpRetryDemo {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        ExtraCnx cnx = new ExtraCnx();
        // send POST
        Request req;
        req = new Request("http://127.0.0.1:11111").retry(3).setConnectionTimeout(1000);
        try {
            System.out.println(Http.execute(req, cnx).asString());
        } finally {
            System.out.println("execute cnx => " + cnx);
            Http.shutdown();
        }
    }

}

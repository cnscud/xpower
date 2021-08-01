package demo;

import com.cnscud.xpower.http.Charsets;
import com.cnscud.xpower.http.Http;
import com.cnscud.xpower.http.Request;
import com.cnscud.xpower.http.Response;

/**
 * @author adyliu (imxylz@gmail.com)
 * @since 2013-11-22
 */
public class SohuDemo {
    public static void main(String[] args) throws Exception{
        Request request = new Request("http://i.sohu.com/u/imxylz");
        request.setEncoding(Charsets.GBK);

        Response response = Http.execute(request);
        System.out.println(response.asString());
        //
        System.out.println("==============");
        System.out.println(response.asString(Charsets.GBK));
    }
}

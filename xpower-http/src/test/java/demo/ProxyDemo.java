/**
 * 
 */
package demo;

import java.time.LocalTime;

import com.cnscud.xpower.http.Request;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2014年5月6日
 */
public class ProxyDemo {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        System.out.println(LocalTime.now());
        try {
            String s = new Request("https://twitter.com").setConnectionTimeout(10*1000) .withHttpProxy("192.168.6.21", 3128).get().asString();
            System.out.println(s);
        } finally {
            System.out.println(LocalTime.now());

        }
    }
}

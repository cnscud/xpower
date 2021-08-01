package demo;

import java.util.Arrays;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2019-07-23
 */
public class StringDemo {

    public static void main(String[] args) {
        String s = "redis://192.168.6.22:7000    \n"
                + "redis://192.168.6.22:7001\tredis://192.168.6.22:7002 redis://192.168.6.22:7003 redis://192.168.6.22:7004 redis://192.168.6.22:7005";
        System.out.println(Arrays.toString(s.split("\\s+")));
    }

}

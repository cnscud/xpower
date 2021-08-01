package test;

import java.util.Map;
import java.util.Properties;

/**
 * @author Deacon Peng 2020-07-30 14:51
 * @version 1.0.0
 */
public class Test {


    @org.junit.Test
    public void test(){

        String zk_hosts = System.getenv("ZK_HOSTS");
        Map<String, String> getenv = System.getenv();
        System.out.println();



        Properties properties = System.getProperties();
        System.out.println();
    }
}

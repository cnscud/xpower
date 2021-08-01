package demo;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.Map;
import java.util.Properties;

/**
 * Test.
 *
 * @author Felix Zhang 2016-12-28 16:14
 * @version 1.0.0
 */
public class FindCommandDemo {

    public static void main(String[] args) {
        String name = "pngquant";

        String osname = System.getProperty("os.name");


        String path = System.getenv("PATH");
        String[] pathList = StringUtils.split(path, File.pathSeparator);
        boolean find = false;

        for(String onedir: pathList){
            File tool = new File(onedir , name);
            if(tool.exists() && tool.canExecute()){
                find = true;
                break;
            }
        }


        Properties ps = System.getProperties();
        for(Object key: ps.keySet()){
            //System.out.println(key +  ":" + ps.get(key));
        }

        Map<String, String> env = System.getenv();
        for(String key : env.keySet()){
            //System.out.println(key +  ":" + env.get(key));
        }
    }
}

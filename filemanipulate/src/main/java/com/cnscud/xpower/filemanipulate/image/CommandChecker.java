package com.cnscud.xpower.filemanipulate.image;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 命令检查.
 *
 * @author Felix Zhang 2016-12-28 17:08
 * @version 1.0.0
 */
public class CommandChecker {

    private static Log log = LogFactory.getLog(CommandChecker.class);

    private static Map<String, Boolean> commandAvailableStatus = new HashMap<>();
    private static Map<String, Boolean> commandCheckedStatus = new HashMap<>();

    public static boolean readCommandAvailableStatus(String name){
        if(commandCheckedStatus.get(name) !=null && commandCheckedStatus.get(name)){
            return commandAvailableStatus.get(name);
        }
        else {
            boolean available = checkCommandReady(name);
            commandAvailableStatus.put(name, available);
            commandCheckedStatus.put(name ,true);

            return available;
        }
    }

    public static boolean isCommandAvailable(String name){
        String path = System.getenv("PATH");
        String[] pathList = StringUtils.split(path, File.pathSeparator);
        boolean find = false;

        for(String onedir: pathList){
            File tool = new File(onedir , name);
            if(tool.exists() && tool.canExecute()){
                find = true;
                log.info("find command: " + name + " under " + tool.getPath());
                break;
            }
        }

        return find;
    }

    public static boolean checkCommandReady(String name){
        String osName = System.getProperty("os.name");
        if(osName == null){
            return false;
        }
        else if(osName.equalsIgnoreCase("Linux")){
            return true; //强制返回, 报错就修复啦
        }
        else {
            return isCommandAvailable(name);
        }
    }
}

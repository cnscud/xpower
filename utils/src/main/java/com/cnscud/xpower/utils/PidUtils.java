package com.cnscud.xpower.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * Java进程PID工具类
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-11-19
 */
public class PidUtils {

    private static final int pid = getPid0();

    /**
     * 获取Java进程的PID（仅仅在SUN VM上有效）
     * 
     * @return Java进程PID，如果获取不到则返回-1
     */
    public static int getPid() {
        return pid;
    }

    private static int getPid0() {
        try {
            RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
            String name = runtime.getName(); //"pid@hostname"  
            return Integer.parseInt(name.substring(0, name.indexOf('@')));
        } catch (Exception e) {
            return -1;
        }
    }
}
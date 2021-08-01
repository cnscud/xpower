package com.cnscud.xpower.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;

/**
 * IP地址工具类
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2012-11-19
 */
public class IpAddressUtils {

    private static String localAddress = null;

    private static String localSiteAddress = null;

    private static String localHostName = null;

    /**
     * 获取本机的主机名称
     * 
     * @return 主机名称
     */
    public static String getLocalHostName() {
        if (localHostName == null) {
            try {
                localHostName = InetAddress.getLocalHost().getHostName();
            } catch (Exception e) {
                localHostName = "UnknowAddress";
            }
        }
        return localHostName;
    }

    /**
     * 获取所有私有地址信息（不包括公网IP地址信息），以'-'分隔多个私有地址，不包括回环地址。当且仅当无法获取私有地址时返回回环地址(
     * 127.0.0.1)
     * <p>
     * 私有地址包括如下三类：
     * <ul>
     * <li>A类 10.0.0.0 --10.255.255.255</li>
     * <li>B类 172.16.0.0--172.31.255.255</li>
     * <li>C类 192.168.0.0--192.168.255.255</li>
     * </ul>
     * 忽略IPv6的所有地址
     * </p>
     * 
     * @return 所有私有地址信息
     */
    public static String getAllLocalSiteAddress() {
        if (localSiteAddress != null) {
            return localSiteAddress;
        }
        String addrs = "";
        try {
            for (NetworkInterface ifc : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (ifc.isUp() && !ifc.isLoopback()) {
                    for (InetAddress addr : Collections.list(ifc.getInetAddresses())) {
                        if (addr instanceof Inet4Address && addr.isSiteLocalAddress()) {//私有地址
                            addrs += "-" + addr.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            //ignore all exception
        }
        localSiteAddress = addrs.length() > 0 ? addrs.substring(1) : "127.0.0.1";
        return localSiteAddress;
    }
    
    /**
     * 获取本机的所有IP地址列表 ，包括公网地址和私有地址，如果仅仅想获取私有地址，请使用
     * {@link #getAllLocalSiteAddress()}<br/>
     * 如果获取不到地址则返回回环地址"127.0.0.1"</br> 忽略IPv6的所有地址
     * 
     * @return 本机所有IP地址，例如"10.11.105.106-201.106.0.20"
     * @see {@link #getAllLocalSiteAddress()}
     */
    public static String getAllLocalAddress() {
        if (localAddress != null) {
            return localAddress;
        }
        String addrs = "";
        try {
            for (NetworkInterface ifc : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (ifc.isUp() && !ifc.isLoopback()) {
                    for (InetAddress addr : Collections.list(ifc.getInetAddresses())) {
                        if (addr instanceof Inet4Address) {
                            addrs += "-" + addr.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            //ignore all exception
        }
        localAddress = addrs.length() > 0 ? addrs.substring(1) : "127.0.0.1";
        return localAddress;
    }
}
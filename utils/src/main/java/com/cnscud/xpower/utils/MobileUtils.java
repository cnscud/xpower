package com.cnscud.xpower.utils;

import org.apache.commons.lang.StringUtils;

/**
 * 手机号处理.
 *
 * @author Felix Zhang 2019-08-01 17:25
 * @version 1.0.0
 */
public class MobileUtils {
    public static String mask(String mobile){
        if(StringUtils.isEmpty(mobile)){
            return mobile;
        }

        if(mobile.length()>8){
            return mobile.substring(0,4) + "****" + mobile.substring(8);
        }
        else {
            return "****";
        }
    }

    public static void main(String[] args) {
        System.out.println(mask("13911716090"));
        System.out.println(mask("23423"));
    }
}


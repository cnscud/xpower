package com.cnscud.xpower.utils;

import org.apache.commons.lang.StringUtils;

import java.util.regex.Pattern;

/**
 * 字符判断类.
 * <p>
 * maybe you need EmojiUtils
 * maybe you need org.apache.commons.lang.CharUtils
 *
 * @author Felix Zhang 2017-01-05 20:11
 * @version 1.0.0
 */
public class CharUtils {

    static String chinaTextRegex = "^[a-zA-Z0-9 ~`:\"';<>?,./\\-=!@\\#$%^&*()_+\\[\\]{}\\\\|\u4e00-\u9fa5]+$";
    static Pattern chinaTextPattern = Pattern.compile(chinaTextRegex);

    @Deprecated
    public static boolean isPrintableChinaText(String str) {
        if (StringUtils.isBlank(str))
            return true;
        return chinaTextPattern.matcher(str).matches();
    }

    @Deprecated
    public static String filterUnsupportChinaText(String str) {
        String okRegex = "[^a-zA-Z0-9 ~`:\"';<>?,./\\-=!@\\#$%^&*()_+\\[\\]{}\\\\|\u4e00-\u9fa5]";
        str = str.replaceAll(okRegex, "");
        return str;
    }


    //仅支持中文和ascii可见字符, 用于发送短信等场景 @fixme 不全 暂时弃用
    public static String filterNotChinaText(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(str.length());
        for (char c : str.toCharArray()) {
            if ((c >= 32 && c <= 126) || (c >= '\u4e00' && c <= '\u9fa5')) {
                buf.append(c);
            }
        }
        return buf.toString();
    }


    //过滤四字节的UTF-8字符, 支持更多语言
    public static String filterSurrogateText(String str) {
        if (str == null || str.length() == 0) {
            return "";
        }

        StringBuilder buf = new StringBuilder(str.length());
        char[] cs = str.toCharArray();
        for (char c : cs) {
            if (!Character.isSurrogate(c)) {
                buf.append(c);
            }
        }

        return buf.toString();
    }

}

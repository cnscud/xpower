/**
 * 
 */
package com.cnscud.xpower.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * HTML相关的一些工具类
 * 
 * @author adyliu(imxylz@gmail.com)
 * @since 2012-12-29
 */
public class HtmlUtils {
    private static final Set<String> NO_END_TAGS = new HashSet<String>(Arrays.asList("img", "br", "hr"));

    /**
     * HTML截断，不破坏HTML代码的结构
     * 
     * @param html
     *            html代码
     * @param size
     *            截断长度
     * @return 截断后的HTML
     * @see https://github.com/getpelican/pelican/blob/master/pelican/utils.py#L353
     */
    public static String subString(String html, int size) {
        if (html == null || html.length() < size)
            return html;
        StringBuilder buf = new StringBuilder(size);
        boolean intag = false;
        String tag = "";
        int count = 0;
        LinkedList<String> tags = new LinkedList<String>();
        for (char c : html.toCharArray()) {
            buf.append(c);
            if (c == '<') {
                intag = true;
            } else if (c == '>') {
                intag = false;
                tag = tag.split("\\s")[0];
                if (tag.charAt(0) == '/') {
                    tag = tag.substring(1);
                    if (!NO_END_TAGS.contains(tag)) {
                        tags.pop();
                    }
                } else {
                    if ('/' != tag.charAt(tag.length() - 1) && !NO_END_TAGS.contains(tag)) {
                        tags.add(tag);
                    }
                }
                tag = "";

            } else {
                if (intag) {
                    tag += c;
                } else {
                    count++;
                    if (count >= size)
                        break;
                }
            }
        }
        //
        while (tags.size() > 0) {
            buf.append("</").append(tags.pop()).append(">");
        }
        return buf.toString();
    }

    /**
     * 轻度过滤危险符号.
     * @param src source
     * @return result
     */
    public static String removeRisk(String src){
        if(src !=null){
            src = src.replaceAll("<|>", "");
            src = src.replaceAll("(?i)script", "");
        }

        return src;
    }

    /**
     * 轻度过滤危险符号.
     * @param src source
     * @return result
     */
    public static String removeMostRisk(String src){
        if(src !=null){
            src = src.replaceAll("<|>|:|\"|%|;|\\(|\\)|&|\\+|'", "");
            src = src.replaceAll("(?i)script", "");
        }

        return src;
    }

    /**
     * 轻度过滤危险符号.
     * @param src source
     * @return result
     */
    public static String removeRisk4Query(String src){
        if(src !=null){
            src = src.replaceAll("<|>", "");
            src = src.replaceAll("(?i)script", "");
        }

        return src;
    }

}

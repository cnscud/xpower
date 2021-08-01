package com.cnscud.xpower.utils;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang.RandomStringUtils.random;

/**
 * 校验.
 *
 * @author Tony Date: 12-10-29 17:30
 */
public class VerifyUtils {

    // static String emailReg = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";

    static String emailReg = "^([a-z0-9A-Z]+[-|\\.|_]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    static String mobileReg = "^1\\d{10}$";
    static String nickReg = "^[a-zA-Z0-9\\u4e00-\\u9fa5_]{2,20}$";
    //static String passwordReg = "^[\\d\\w\\.\\!\\#\\$\\%\\^\\*\\'\\+\\-\\/\\`\\@\\(\\)\\[\\]\\\\\\:\\;\\\"\\,\\<\\>\\?\\=\\_\\{\\|\\}\\~]{6,20}$";
    public static final String PASSWORD_ERROR_MESSAGE = "密码格式错误(8~20位大写字母、小写字母、数字等)";

    //合法的手机号段: 可以接电话/发短信的, 不包括物联网, 无线网卡. 179是测试用.
    // 最新的号码段 by ady
    // https://zh.wikipedia.org/wiki/%E4%B8%AD%E5%9B%BD%E5%86%85%E5%9C%B0%E7%A7%BB%E5%8A%A8%E7%BB%88%E7%AB%AF%E9%80%9A%E8%AE%AF%E5%8F%B7%E6%AE%B5 
    public static ArrayList<String> MobilePrefixs = Lists.newArrayList(
            "130", "131", "132", "133", "134", "135", "136", "137", "138", "139",
            "145","147", "149",
            "150", "151", "152", "153", "155", "156", "157", "158", "159",
            "165",
            "166",
            "170", "171",
            // "172", 尚未启用
            "173", "174", "175", "176", "177", "178",
            "179",
            "180", "181", "182", "183", "184", "185", "186", "187", "188", "189",
            "191",
            "198", "199"
    );


    public static boolean verifyEmail(String email) {
        return email != null && email.contains("@") && email.matches(emailReg);
    }

    public static boolean verifyMobile(String mobile) {
        return mobile != null && mobile.matches(mobileReg) && isChinaMobile(mobile);
    }

    private static boolean isChinaMobile(String mobile) {
        if (mobile.length() < 3) {
            return false;
        }

        String prefix = mobile.substring(0, 3);
        return MobilePrefixs.contains(prefix);
    }

    public static boolean maybeMobile(String mobile) {

        return mobile != null && StringUtils.length(mobile) < 32;
    }

    public static boolean verifyNick(String nick) {
        // 禁止纯数字
        return nick.matches(nickReg) && !NumberUtils.isDigits(nick);
    }

    /**
     * 密码是否合法 （①8~20位，②至少包含一位大写，③至少包含一位小写，④至少包含一位数字 )
     *
     * @author Ady Liu (imxylz@gmail.com)
     * @since 2017年2月3日
     */
    public static boolean verifyPassword(String password) {
        if (password != null && password.length() >= 8 && password.length() <= 20) {
            return StringUtils.containsAny(password, UPPER_LETTER) //
                    && StringUtils.containsAny(password, LOWER_LETTER) //
                    && StringUtils.containsAny(password, NUMBER_LETTER);
        }
        return false;
    }

    /**
     * 过滤昵称（用户名）中的特殊字符
     *
     * @param nick 昵称（用户名）
     * @return 过滤后的昵称（仅保留a-zA-Z中文_)
     * @author Ady Liu (imxylz@gmail.com)
     * @since 2013年7月30日
     */
    public static String filterNick(String nick) {
        if (nick == null || nick.length() == 0) {
            return "";
        }
        StringBuilder buf = new StringBuilder(nick.length());
        for (char c : nick.toCharArray()) {

            if ((c >= 'a' && c <= 'z')//
                    || (c >= 'A' && c <= 'Z')//
                    || (c >= '0' && c <= '9')//
                    || c == '_'//
                    || (c >= '\u4e00' && c <= '\u9fa5')

                    ) {
                buf.append(c);
            }
        }
        return buf.toString();
    }

    static final char[] UPPER_LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    static final char[] LOWER_LETTER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toLowerCase().toCharArray();
    static final char[] NUMBER_LETTER = "0123456789".toCharArray();
    static final char[] SPECIAL_LETTER = "~!@#$%^&*()_+`-=[]\\{}|:\";'<>?,./".toCharArray();
    static final char[] PASSWORD_SPECIAL_LETTER = "~!@#$%^&*()_+-=[]{}|<>".toCharArray();

    /**
     * 生成10位随机密码
     *
     * @return 10位随机密码
     * @author Ady Liu (imxylz@gmail.com)
     * @since 2017年2月3日
     */
    public static String generateRandomPassword() {
        String p = random(1, UPPER_LETTER) + random(1, LOWER_LETTER) + random(8, NUMBER_LETTER);//+ random(2, PASSWORD_SPECIAL_LETTER);
        List<Character> list = new ArrayList<>(Lists.charactersOf(p));
        Collections.shuffle(list);
        return StringUtils.join(list, "");
    }

    public static void main(String[] args) throws Exception {
        String a = "13911716090";
        String b = "19211716090";
        String c = "17011716090";

        System.out.println(isChinaMobile(a));
        System.out.println(isChinaMobile(b));
        System.out.println(isChinaMobile(c));
    }
}

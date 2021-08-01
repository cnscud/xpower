package com.cnscud.xpower.utils;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static java.lang.String.format;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CookieUtils {

    /**
     * Cookie所在的路径 *
     */
    private static String cookiePath = "/";

    final static String SESSION_COOKIE_NAME = "z";

    // 记录最近记录的三个用户id
    final static String LAST_LOGIN_XIDS = "zid";
    final static String COOKIE_NSID = "nsid";
    final static String COOKIE_Z = "z";

    final static int ONE_YEAR = (int) TimeUnit.DAYS.toSeconds(365);
    final static int ONE_DAY = (int) TimeUnit.DAYS.toSeconds(1);
    private static final Logger log = LoggerFactory.getLogger(CookieUtils.class);

    public static final String DEFAULT_DOMAIN = ".cnscud.com";

    private static void logActiveIds(HttpServletRequest req, HttpServletResponse resp, String xid, String domain) {
        String value = getCookieValue(req, LAST_LOGIN_XIDS);
        if (value == null || !value.equals(xid)) {
            setCookie(resp, LAST_LOGIN_XIDS, xid, null, null, ONE_YEAR);
        }
    }


    /**
     * 从cookie中读取nsid
     *
     * @param request HTTP 请求
     * @return nsid值或者null
     * @author Ady Liu (imxylz@gmail.com)
     * @see #parseNsid(String)
     * @since 2013年11月6日
     */
    public static String readNsid(HttpServletRequest request) {
        String nsid = getCookieValue(request, COOKIE_NSID);
        return parseNsid(nsid);
    }

    public static String readTrackIdByNsid(HttpServletRequest request) {
        String nsid = readNsid(request);
        if (StringUtils.isNotBlank(nsid)) {
            return "nsid:" + nsid;
        }

        return "";
    }

    /**
     * 解析nsid的值
     *
     * @param cookie_nsid cookie_nsid
     * @return 32位字符串的nsid值，与nginx保持一致
     * @author Ady Liu (imxylz@gmail.com)
     * @see #readNsid(HttpServletRequest)
     * @since 2013年11月6日
     */
    public static String parseNsid(String cookie_nsid) {
        if (!StringUtils.isEmpty(cookie_nsid)) {
            // String nsid = "wKgGF1JmHCs7gUgMAwMEAg==";
            try {
                StringBuilder s = new StringBuilder(32);
                ByteBuffer buf = ByteBuffer.wrap(Base64.decodeBase64(cookie_nsid)).order(ByteOrder.LITTLE_ENDIAN);
                while (buf.hasRemaining()) {
                    s.append(String.format("%08X", buf.getInt()));
                }
                return s.toString();
            }
            catch (Exception ex) {
                log.warn(format("error_cookie_nsid (%s)", cookie_nsid));
            }
        }
        return null;
    }

    /**
     * 编码NSID的cookie值
     *
     * @param nsid nsid解码后的32位值，例如：C801060A143B6F529C5F808502230603
     * @return cookie中编码的nsid值
     * @author Ady Liu (imxylz@gmail.com)
     * @since 2015年5月6日
     */
    public static String encodeNsid(String nsid) {
        if (StringUtils.isBlank(nsid)) {
            return null;
        }
        ByteBuffer bb = ByteBuffer.allocate(nsid.length() / 2).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < nsid.length() - 1; i += 8) {
            int x = Integer.parseUnsignedInt(nsid.substring(i, i + 8), 16);
            bb.putInt(x);
        }
        return Base64.encodeBase64String(bb.array());
    }


    /**
     * 删除HttpServletResponse中的z票据（如果有的话)
     *
     * @param cookieDomain cookie种植的域信息
     */
    public static void deletePassport(HttpServletRequest request, HttpServletResponse response, String cookieDomain) {
        deleteCookie(response, getCookie(request, COOKIE_Z), cookieDomain, "/");
    }


    public static void setCookie(HttpServletResponse response, String name, String value, String domain) {
        CookieUtils.setCookie(response, name, value, domain, null, -1);
    }

    public static void setCookie(HttpServletResponse response, String name, String value, String domain, String path, int maxAgeInSecond) {

        if (name == null || value == null) {
            log.warn("name or value can't be null, name=" + name + " value=" + value);
            return;
        }
        log.debug("setCookie name=[{}] value=[{}],domain=[{}], maxAge=[{}]", name, value, domain, maxAgeInSecond);
        Cookie cookie = new Cookie(name, value);
        cookie.setSecure(false);

        if (domain == null) {
            domain = DEFAULT_DOMAIN;
        }
        cookie.setDomain(domain);

        path = (path == null ? "/" : path);
        cookie.setPath(path);

        cookie.setMaxAge(maxAgeInSecond);
        response.addCookie(cookie);
    }

    /**
     * 获取全部的Cookie值
     *
     * @param request Http请求
     * @return 全部的cookie名称和值
     * @author Ady Liu (imxylz@gmail.com)
     * @since 2016年12月20日
     */
    public static Map<String, String> getCookies(HttpServletRequest request) {
        Map<String, String> m = new LinkedHashMap<>();
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                m.put(c.getName(), c.getValue());
            }
        }
        return m;
    }

    /**
     * Convenience method to get a cookie by name
     *
     * @param request the current request
     * @param name    the name of the cookie to find
     * @return the cookie (if found), null if not found
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        Cookie returnCookie = null;
        if (cookies == null) {
            return returnCookie;
        }
        for (int i = 0; i < cookies.length; i++) {
            Cookie thisCookie = cookies[i];
            if (thisCookie.getName().equals(name)) {
                // cookies with no value do me no good!
                if (!thisCookie.getValue().equals("")) {
                    returnCookie = thisCookie;
                    break;
                }
            }
        }
        return returnCookie;
    }

    /**
     * 获取cookie值
     *
     * @param request HTTP请求
     * @param name    cookie名称
     * @return null为不存在或者cookie值
     */
    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        if (cookie == null)
            return null;
        return cookie.getValue();
    }

    /**
     * Convenience method for deleting a cookie by name
     *
     * @param response the current web response
     * @param cookie   the cookie to delete
     * @param path
     */
    public static void deleteCookie(HttpServletResponse response, Cookie cookie, String domain, String path) {
        if (cookie != null) {
            // Delete the cookie by setting its maximum age to zero
            cookie.setMaxAge(0);

            if (log.isDebugEnabled()) {
                log.debug("deleteCookie(HttpServletResponse, Cookie) -  : cookie=" + cookie); //$NON-NLS-1$
            }

            if (domain == null) {
                domain = DEFAULT_DOMAIN;
            }
            cookie.setDomain(domain);
            cookie.setValue("");

            path = (path == null ? "/" : path);
            cookie.setPath(path);

            response.addCookie(cookie);
        }
    }

}
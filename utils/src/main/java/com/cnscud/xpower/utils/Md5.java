package com.cnscud.xpower.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public class Md5 {

    final static char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * Encodes a string
     * 
     * @param str String to encode
     * @return Encoded String
     */
    public static String crypt(String str) {
        return crypt(str, "UTF-8");
    }

    /**
     * encode a string with given encoding
     * 
     * @param str String to encode
     * @param charSet encoding
     * @return the encode String
     */
    public static String crypt(String str, String charSet) {
        if (str == null || str.length() == 0 || charSet == null) {
            throw new IllegalArgumentException("String or charset to encript cannot be null or zero length");
        }
        try {
            return crypt(str.getBytes(charSet));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }


    /**
     * encode bytes
     * 
     * @param source source bytes
     * @return the encode String
     */
    public static String crypt(byte[] source) {
        String s = null;

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];

                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return s;
    }

    //crypt处理大字符串时太耗内存， 增加此方法支持在外部使用stream处理MessageDigest
    public static String cryptDigest(MessageDigest md) {
        String s = null;

        try {
            byte tmp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];

                str[k++] = hexDigits[byte0 & 0xf];
            }
            s = new String(str);

        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return s;
    }

}
package com.cnscud.xpower.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 加密token的处理工具
 *
 * @author pengxia
 * @author adyliu (adyliu@sohu-inc.com)
 */
public class SecretDesUtils {

    public static String Algorithm = "TripleDES";

    private static Cipher decrypt = null;

    private static String ENCODING = "UTF-8";

    private static Cipher encrypt = null;

    public static String EncryptMessage = "0102-uw-n*yt@mjtxqz8-px";

    private static boolean isDecryptInit = false;

    private static boolean isEncryptInit = false;

    private static String KEYSTR = "0209-qi-m*ng-h@njian9-xp";

    private static final Log log = LogFactory.getLog(SecretDesUtils.class);

    public static String SPLITTER = "|";
    /**
     * 解密字符串（Base64+DES)
     * @param inputByte 原始字符串
     * @return 解密后原始字符串
     */
    public static String decrypt(byte[] inputByte) {
        try {
            byte[] encodeStr = XBase64Utils.decode(inputByte, 0, inputByte.length);
            byte[] ciperByte = getDecrypt().doFinal(encodeStr);
            return new String(ciperByte, ENCODING);
        } catch (Exception e) {
            log.error("Can't decrypt : " + e.getMessage());
        }
        return null;
    }
    /**
     * 解密字符串（Base64+DES)
     * @param s 原始字符串
     * @return  解密后原始字符串
     */
    public static String decrypt(String s) {
        if (s == null) {
            return null;
        }
        try {
            return decrypt(s.getBytes(ENCODING));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 解密字符串（URLDecode+BASE64+DES)
     * @param url 加密后的字符串
     * @return 原始字符串
     */
    public static String decryptURL(String url) {
        if(url == null) {
            return null;
        }
        try {
            String encrStr = java.net.URLDecoder.decode(url, ENCODING);
            return decrypt(encrStr.getBytes(ENCODING));
        } catch (Exception e) {
            log.error("Can't decrypt : " + e.getMessage());
        }
        return null;
    }
    /**
     * 加密字符串（DES+BASE64)
     * @param inputByte 原始字节
     * @return 加密后字符串
     */
    public static String encrypt(byte[] inputByte) {
        try {
            byte[] ciperByte = getEncrypt().doFinal(inputByte);
            String encodeStr = XBase64Utils.encodeBytes(ciperByte, 0, ciperByte.length, XBase64Utils.DONT_BREAK_LINES);
            //encodeStr = java.net.URLEncoder.encode(encodeStr, ENCODING);
            return encodeStr;
        } catch (Exception e) {
            log.error("Can't encrypt : " + e.getMessage());
        }
        return null;
    }
    /**
     * 加密字符串（DES+BASE64)
     * @param s 原始字符串
     * @return 加密后的字符串
     */
    public static String encrypt(String s) {
        if (s == null) {
            return null;
        }
        try {
            return encrypt(s.getBytes(ENCODING));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 加密字符串（DES+BASE64+URLEncode)
     *
     * @param url 原始字符串
     * @return 加密后字符串
     */
    public static String encryptURL(String url) {
        if(url == null) {
            return null;
        }
        try {
            String encodeStr = encrypt(url.getBytes(ENCODING));
            return java.net.URLEncoder.encode(encodeStr, ENCODING);
            //return encodeStr;
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }

    private static Cipher getDecrypt() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        if (decrypt == null && !isDecryptInit) {
            synchronized (SecretDesUtils.class) {
                if (decrypt == null && !isDecryptInit) {
                    decrypt = Cipher.getInstance(Algorithm);
                    decrypt.init(Cipher.DECRYPT_MODE, getKey());
                    isDecryptInit = true;
                }
            }

        }
        return decrypt;
    }

    private static Cipher getEncrypt() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        if (encrypt == null && !isEncryptInit) {
            synchronized (SecretDesUtils.class) {
                if (encrypt == null && !isEncryptInit) {
                    encrypt = Cipher.getInstance(Algorithm);
                    encrypt.init(Cipher.ENCRYPT_MODE, getKey());
                    isEncryptInit = true;
                }
            }

        }
        return encrypt;
    }

    private static Key getKey() {
        SecretKey key = new SecretKeySpec(KEYSTR.getBytes(), Algorithm);
        return key;
    }

}

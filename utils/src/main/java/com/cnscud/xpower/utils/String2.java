package com.cnscud.xpower.utils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * @version 1.0.0
 * @author: tonychen@shijiebang.net Date: 14-8-25 上午10:50
 */
public class String2 {

    // 该片段来自于http://www.codesnippet.cn/detail/211120137401.html

    static public String filterOffUtf8Mb4(String text) throws UnsupportedEncodingException {
        byte[] bytes = text.getBytes("utf-8");
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        int i = 0;
        while (i < bytes.length) {
            short b = bytes[i];

            if (b > 0) {
                buffer.put(bytes[i++]);
                continue;
            }
            b += 256;
            if ((b ^ 0xC0) >> 4 == 0) {
                buffer.put(bytes, i, 2);
                i += 2;
            } else if ((b ^ 0xE0) >> 4 == 0) {
                buffer.put(bytes, i, 3);
                i += 3;
            } else if ((b ^ 0xF0) >> 4 == 0) {
                i += 4;
            }
        }
        buffer.flip();
        return new String(buffer.array(), "utf-8");
    }

    /**
     * 过滤4字节的UTF-8字符
     * 
     * @param cs
     *            字符序列
     * @return 过滤后的字符序列
     * @since 2015年3月23日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public static StringBuilder filterUtf8mb4(CharSequence cs) {
        StringBuilder buf = new StringBuilder(cs.length());
        for (int i = 0; i < cs.length(); i++) {
            char c = cs.charAt(i);
            if (!Character.isSurrogate(c)) {
                buf.append(c);
            }
        }
        return buf;
    }
}

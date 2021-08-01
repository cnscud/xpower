package com.cnscud.xpower.filemanipulate.store;

import org.apache.commons.lang.time.DateFormatUtils;

import java.util.Date;

/**
 * Store file utils.
 *
 * @author Felix Zhang  Date 2012-10-24 18:36
 * @version 1.0.0
 */
public class DeployFileUtils {

    public static String getUploadDirectoryNames(String shortPrefix) {
        Date now = new Date();

        return getUploadDirectoryNames(shortPrefix, now);
    }

    public static String getUploadDirectoryNames(String shortPrefix, Date date) {
        return shortPrefix == null ? DateFormatUtils.format(date, "yy/MMdd/HHmm") : (shortPrefix + DateFormatUtils
                .format(date, "yy/MMdd/HHmm"));
    }
}

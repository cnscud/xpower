package com.cnscud.xpower.filemanipulate.image;

import junit.framework.TestCase;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import java.util.Date;

/**
 * for test.
 *
 * @author Felix Zhang 2016-12-28 10:21
 * @version 1.0.0
 */
public class BaseTest extends TestCase {

    protected String testfileDirectory = "./testfile";
    protected String testtempfileDir = "./testfile/temp/";
    protected String userPrefix = "12345"; //for test


    protected String pickImageDirectoryName(String typeShortName, String imageId, String userPrefix) {
        return typeShortName + DateFormatUtils.format(new Date(), "yyyyMMddHHmm") + (userPrefix == null ? imageId : (userPrefix + imageId));
    }

    protected String pickNewImageId() {
        return RandomStringUtils.randomAlphabetic(5).toLowerCase(); // 随机id
    }


    protected String pickThumbnailImageFileName(String imageId, String extension, String userPrefix, String sizeName, String sizeInfo) {
        if (sizeInfo != null && sizeInfo.length() > 0) {
            return (userPrefix == null ? imageId : (userPrefix + imageId)) + "_" + sizeInfo + "_" + sizeName + "." + extension;
        }
        else {
            return (userPrefix == null ? imageId : (userPrefix + imageId)) + "_" + sizeName + "." + extension;
        }
    }

}

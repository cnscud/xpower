package com.cnscud.xpower.filemanipulate.image;

import java.io.File;

import com.cnscud.xpower.filemanipulate.image.impl.ImageInfoCheckerImpl;

/**
 * Image Info Support.
 * 
 * @author Felix Zhang Date: 2009-1-20 14:52:09
 * @Version: 1.0.0
 */
public class ImageInfoUtils {

    public static IImageInfoChecker checkImageInfo(File file) {
        try {
            ImageInfoCheckerImpl iici = new ImageInfoCheckerImpl();
            if (iici.check(file)) {
                return iici;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

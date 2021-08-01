/**
 * 
 */
package com.cnscud.xpower.filemanipulate.image;

import java.io.File;

/**
 * 图像检测工具
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2013年6月18日
 */
public interface IImageInfoChecker {

    int getWidth();

    int getHeight();

    String getFormatName();

    boolean check(File file) throws Exception;
}

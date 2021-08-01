package com.cnscud.xpower.filemanipulate.image.engager;

import com.cnscud.xpower.filemanipulate.image.IImageSize;

import java.io.File;
import java.io.IOException;

/**
 * Image Builder Interface.
 *
 * @author Felix Zhang 2016-12-20 21:42
 * @version 1.0.0
 */
public interface ImageEngager {

    String getName();
    boolean available();


    /**
     * resize图像的默认实现.
     * @param srcFile 原文件
     * @param expectSize 目标尺寸要求
     * @param destFile 目标文件
     * @param watermark 水印
     * @param watermarkPosition 水印位置
     * @throws IOException 异常
     */
    void resizeImage(int srcwidth, int srcheight, File srcFile, IImageSize expectSize, File destFile, String watermark, String watermarkPosition) throws IOException;

    /**
     * resize图像, 默认情况下不会把小图像拉大, 只会保持图片尺寸.
     *
     * @param srcFile 原文件
     * @param expectSize 目标尺寸要求
     * @param destFile 目标文件
     * @param watermark 水印
     * @param watermarkPosition 水印位置
     * @throws IOException 异常
     */
    void resizeImageNoEnlarge(int srcwidth, int srcheight, File srcFile, IImageSize expectSize, File destFile, String watermark, String watermarkPosition) throws IOException;

    /**
     *
     * resize图像, 如果图像比例不合适, 会进行裁剪并按比例充满规定尺寸.
     *
     * warning: 各个实现导致的结果可能不一致. 但要保证生成的图像是按比例的, 不失真,但是被裁剪了.
     *
     * @param srcwidth 原图宽
     * @param srcheight 原图高
     * @param srcFile 原文件
     * @param expectSize 目标尺寸要求
     * @param destFile 目标文件
     * @param watermark 水印
     * @param watermarkPosition 水印位置
     * @throws IOException 异常
     */
    void resizeFullFilledImage(int srcwidth, int srcheight, File srcFile, IImageSize expectSize, File destFile, String watermark, String watermarkPosition) throws IOException;

    /**
     * 转换图像格式, 或者控制图片质量, 实际无resize.
     * @param srcFile 原文件
     * @param destFile 目标文件
     * @throws IOException 异常
     */
    void transferImage(File srcFile, File destFile) throws IOException;

}

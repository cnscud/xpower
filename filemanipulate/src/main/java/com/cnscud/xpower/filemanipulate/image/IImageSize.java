/**
 * 
 */
package com.cnscud.xpower.filemanipulate.image;

/**
 * 图像尺寸 (https://f7.sjbly.cn/y16/1205/1638/00qxe_630x420_fs.jpg)
 * 
 * @author adyliu(imxylz@gmail.com)
 * @since 2013-1-18
 */
public interface IImageSize extends Comparable<IImageSize> {
    /**
     * 图像后缀，例如：fs/fc/fb/m/b 等
     * @return 图像后缀，其它尺寸依赖这个这个后缀推算
     */
    String getSuffix();
    /**
     * 图像宽度，例如：630x420中的630
     * @return 图像宽度
     */
    int getWidth();
    /**
     * 图像高度，例如：630x420中的420
     * @return 图像高度
     */
    int getHeight();

    boolean isForce();

    @Override
    int compareTo(IImageSize o);
    
}

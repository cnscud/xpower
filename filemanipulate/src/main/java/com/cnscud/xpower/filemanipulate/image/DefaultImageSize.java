/**
 * 
 */
package com.cnscud.xpower.filemanipulate.image;

/**
 * 默认的图像尺寸对象
 * 
 * @author adyliu(imxylz@gmail.com)
 * @since 2013-1-18
 */
public class DefaultImageSize implements IImageSize {
    final String suffix;
    final int width;
    final int height;
    final boolean force;

    public DefaultImageSize(String suffix, int width, int height) {
        this.suffix = suffix;
        this.width = width;
        this.height = height;
        this.force = false;
    }

    public DefaultImageSize(String suffix, int width, int height, boolean force) {
        this.suffix = suffix;
        this.width = width;
        this.height = height;
        this.force = force;
    }

    @Override
    public String getSuffix() {
        return suffix;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public boolean isForce() {
        return force;
    }

    @Override
    public DefaultImageSize clone() throws CloneNotSupportedException {
        return (DefaultImageSize) super.clone();
    }

    @Override
    public int compareTo(IImageSize o) {
        int gap = width - o.getWidth();
        if (gap == 0) {
            gap = height - o.getHeight();
        }
        return gap;
    }

    @Override
    public String toString() {
        return String.format("%s: %dx%d", this.suffix, this.width, this.height);
    }

}

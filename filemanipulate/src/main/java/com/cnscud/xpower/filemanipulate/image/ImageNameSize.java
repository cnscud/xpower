package com.cnscud.xpower.filemanipulate.image;

/**
 * Image size, name, url, filename.
 * 
 * @author Felix Zhang Date 2012-10-23 15:08
 * @version 1.0.0
 */
public class ImageNameSize implements IImageSize, Cloneable {

    private String name; // 后缀名
    private int width; // 宽
    private int height; // 高
    private boolean force = false;

    private boolean supportAnimatiom = true; // 是否支持动画, 默认支持. 以后可能出现缩略图不能动画, 大图可以的情况

    private String filename; // 纯文件名, 无路径
    private String url; // 完整网址

    public ImageNameSize() {
    }

    public ImageNameSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public ImageNameSize(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
    }

    public ImageNameSize(String name, int width, int height, boolean force) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.force = force;
    }

    public ImageNameSize(String name, int width) {
        this.name = name;
        this.width = width;
    }

    public boolean isSupportAnimatiom() {
        return supportAnimatiom;
    }

    public void setSupportAnimatiom(boolean supportAnimatiom) {
        this.supportAnimatiom = supportAnimatiom;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    public static ImageNameSize n(String name, int width, int height) {
        return new ImageNameSize(name, width, height);
    }

    public static ImageNameSize n(String name, int width, int height, boolean force) {
        return new ImageNameSize(name, width, height, force);
    }

    public static ImageNameSize n(int width, int height) {
        return new ImageNameSize(width, height);
    }

    public static ImageNameSize n(String name, int width) {
        return new ImageNameSize(name, width);
    }

    public static ImageNameSize[] convert(IImageSize[] sizes) {
        ImageNameSize[] ret = new ImageNameSize[sizes.length];
        for (int i = 0; i < sizes.length; i++) {
            ret[i] = ImageNameSize.n(sizes[i].getSuffix(), sizes[i].getWidth(), sizes[i].getHeight(), sizes[i].isForce());
        }
        return ret;
    }

    public ImageNameSize clone() {
        ImageNameSize o = null;
        try {
            o = (ImageNameSize) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }

    @Override
    public String getSuffix() {
        return name;
    }

    @Override
    public int compareTo(IImageSize o) {
        int gap = width - o.getWidth();
        if (gap == 0) {
            gap = height - o.getHeight();
        }
        return gap;
    }

}

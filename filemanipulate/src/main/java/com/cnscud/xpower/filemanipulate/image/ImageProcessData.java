package com.cnscud.xpower.filemanipulate.image;

/**
 * Image File Result.
 *
 * @author Felix Zhang  Date 2012-10-23 17:53
 * @version 1.0.0
 */
public class ImageProcessData {
    private String imageId; //图像id
    private String sourceImageName; //原图文件名, 无路径
    private String sourceImageUrl; //原图完整网址
    private String dimension; //300x500
    private long size; //文件存储大小

    private String rawSourceImageName; //一般情况下没有, 只有创建一个中间源图时才存在这个信息

    private String parentFileDirectory; //文件所在目录

    private ImageNameSize[] thumbnailImages; //几种尺寸的图像的信息

    private String shortType; //业务自定义类型, 用作图像目录前缀

    public String getRawSourceImageName() {
        return rawSourceImageName;
    }

    public void setRawSourceImageName(String rawSourceImageName) {
        this.rawSourceImageName = rawSourceImageName;
    }

    public String getShortType() {
        return shortType;
    }

    public void setShortType(String shortType) {
        this.shortType = shortType;
    }

    public String getSourceImageUrl() {
        return sourceImageUrl;
    }

    public void setSourceImageUrl(String sourceImageUrl) {
        this.sourceImageUrl = sourceImageUrl;
    }

    public String getParentFileDirectory() {
        return parentFileDirectory;
    }

    public void setParentFileDirectory(String parentFileDirectory) {
        this.parentFileDirectory = parentFileDirectory;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getSourceImageName() {
        return sourceImageName;
    }

    public void setSourceImageName(String sourceImageName) {
        this.sourceImageName = sourceImageName;
    }

    public ImageNameSize[] getThumbnailImages() {
        return thumbnailImages;
    }

    public void setThumbnailImages(ImageNameSize[] thumbnailImages) {
        this.thumbnailImages = thumbnailImages;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}

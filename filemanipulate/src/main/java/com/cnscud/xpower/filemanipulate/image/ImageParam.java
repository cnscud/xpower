package com.cnscud.xpower.filemanipulate.image;

import com.cnscud.xpower.filemanipulate.ManipulateConstants;

import java.io.File;

/**
 * 图像处理参数
 * <p>
 * 对于一张图片而言：https://f7.sjbly.cn/y16/1205/1638/00qxe_630x420_fs.jpg
 * 
 * </p>
 * @author adyliu (imxylz@gmail.com)
 * @since 2016年12月20日
 */
public class ImageParam {

    File file;
    IImageSize[] requiredSizeImages;
    String userPrefix;
    /**
     * 图片的前缀
     * @see ManipulateConstants#ShortName_Visa
     * @see ManipulateConstants#ShortName_GeneralImage
     */
    String customType;
    /**是否进行原图裁切，如果原图大于2000x2000，则先裁切2000x2000并保存为原图 */
    boolean baseWebSourceIfSupport = true;//2000x2000
    boolean keepImageFormat = false;
    /**结果需要返回的尺寸*/
    IImageSize minSize;
    /**水印配置*/
    String watermarkConfig;
    /**返回的结果后缀，用于请求结束后返回的URL*/
    String returnSuffix;
    /**是否需要认证，返回的资源URL需要授权才能访问*/
    boolean needAuthenticate = false;
    boolean keepSourceImage = false;
    /**
     * 裁切、上传一张照片
     * @param file 文件信息
     * @param requiredSizeImages 需要裁切的尺寸 对应 fs:630x420
     * @param userPrefix 文件名前缀 对应 00
     * @param biz 自定义类型 对应 y,签证使用visa
     */
    public ImageParam(File file, IImageSize[] requiredSizeImages, String userPrefix, String biz) {
        this.file = file;
        this.requiredSizeImages = requiredSizeImages;
        this.userPrefix = userPrefix;
        this.customType = biz;
    }

    /**使用默认的水印*/
    public ImageParam withDefaultWatermarkConfig() {
        this.watermarkConfig = "wm1.png|BOTTOM_RIGHT";
        return this;
    }
    
    public File getFile() {
        return file;
    }

    public IImageSize[] getRequiredSizeImages() {
        return requiredSizeImages;
    }

    public String getUserPrefix() {
        return userPrefix;
    }

    public String getCustomType() {
        return customType;
    }

    public boolean isBaseWebSourceIfSupport() {
        return baseWebSourceIfSupport;
    }

    public boolean isKeepImageFormat() {
        return keepImageFormat;
    }

    public boolean isNeedAuthenticate() {
        return needAuthenticate;
    }
    public ImageParam setNeedAuthenticate(boolean needAuthenticate) {
        this.needAuthenticate = needAuthenticate;
        return this;
    }
    public IImageSize getMinSize() {
        return minSize;
    }

    public String getWatermarkConfig() {
        return watermarkConfig;
    }

    public ImageParam setBaseWebSourceIfSupport(boolean baseWebSourceIfSupport) {
        this.baseWebSourceIfSupport = baseWebSourceIfSupport;
        return this;
    }

    public ImageParam setKeepImageFormat(boolean keepImageFormat) {
        this.keepImageFormat = keepImageFormat;
        return this;
    }

    public ImageParam setMinSize(IImageSize minSize) {
        this.minSize = minSize;
        return this;
    }

    public ImageParam setWatermarkConfig(String watermarkConfig) {
        this.watermarkConfig = watermarkConfig;
        return this;
    }
    public String getReturnSuffix() {
        return returnSuffix;
    }
    public ImageParam setReturnSuffix(String returnSuffix) {
        this.returnSuffix = returnSuffix;
        return this;
    }
    public boolean isKeepSourceImage() {
        return keepSourceImage;
    }
    public ImageParam setKeepSourceImage(boolean keepSourceImage) {
        this.keepSourceImage = keepSourceImage;
        return this;
    }
}

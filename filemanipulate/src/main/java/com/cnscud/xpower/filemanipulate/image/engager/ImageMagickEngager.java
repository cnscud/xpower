package com.cnscud.xpower.filemanipulate.image.engager;

import com.cnscud.xpower.filemanipulate.ManipulateConstants;
import com.cnscud.xpower.filemanipulate.image.CommandChecker;
import com.cnscud.xpower.filemanipulate.image.IImageSize;
import com.cnscud.xpower.filemanipulate.image.ImageUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.im4java.core.CompositeCmd;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;

import java.io.File;
import java.io.IOException;

/**
 * 命令行图像处理.
 *
 * 修改历史:
 * done im使用index color palette? -- 太慢, 放弃
 * done 透明 png -> jpg 的背景色问题, 设置为白色
 * done 转换场景判断: png -> png , png -> jpg ?
 * done 测试环境判断
 * done thumbnailtor 对 index png 支持不好
 * done pngquant (source方式) + thumbnaitor 效果不行!!!!!
 * done 裁切填充
 * done 放弃 resize和水印操作合并?
 * done 水印大小判断
 * done 忽略先 -- resample?? 咋弄
 *
 * @author Felix Zhang 2016-12-21 14:06
 * @version 1.0.0
 */
public class ImageMagickEngager implements ImageEngager {

    private Log logger = LogFactory.getLog(getClass());

    public enum WatermarkPosition {
        NorthWest, North, NorthEast, West, Center, East, SouthWest, South, SouthEast, None;

        public static WatermarkPosition get(String name) {
            WatermarkPosition[] values = WatermarkPosition.values();
            for (WatermarkPosition item : values) {
                if (item.name().equals(name)) {
                    return item;
                }
            }
            return WatermarkPosition.None;
        }
    }

    @Override
    public boolean available(){
        return CommandChecker.readCommandAvailableStatus("convert");
    }


    @Override
    public String getName() {
        return "imagemagick";
    }

    @Override
    public void resizeImage(int srcWidth, int srcHeight, File srcFile, IImageSize expectSize, File destFile, String watermark, String watermarkPosition) throws IOException {
        resizeImageNoEnlarge(srcWidth, srcHeight, srcFile, expectSize, destFile, watermark, watermarkPosition);
    }

    @Override
    public void resizeImageNoEnlarge(int srcwidth, int srcheight, File srcFile, IImageSize expectSize, File destFile, String watermark, String watermarkPosition) throws IOException {
        int width = expectSize.getWidth();
        int height = expectSize.getHeight();

        ConvertCmd cmd = new ConvertCmd();

        // create the operation, add images and operators/options
        IMOperation op = new IMOperation();

        op.strip();
        //op.resample(72, 72); //300 dpi -> 72 dpi ? 貌似有副作用, 如果不指定宽高的时候

        op.quality(ManipulateConstants.Default_Quality * 100);
        op.interlace("Line");  //interlaced PNG or GIF or progressive JPEG

        if ((srcwidth <= width && srcheight <= height) || (srcwidth <= width && height <= 0) || (width <= 0 && srcheight <= height)) {
            //nothing
        }
        else if (height <= 0) {
            op.resize(width);
        }
        else if (width <= 0) {
            op.resize(null, height);
        }
        else if (width > 0 && height > 0) {
            if(expectSize.isForce()){
                op.resize(width, height, "!"); //强制拉伸
            }
            else {
                op.resize(width, height); //不强制拉伸
            }
        }
        else {
            logger.error("what wrong? " + width + "x" + height);
        }

        if(needAppendBackground(srcFile, destFile)) {
            op.background("white");
            op.flatten();
        }

        op.addImage(); //src
        op.addImage(); //dest

        // execute the operation
        try {
            if(logger.isDebugEnabled()){
                logger.debug(op.toString());
            }
            cmd.run(op, srcFile.getPath(), destFile.getPath());
        }
        catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }

        //没有水印
        if (StringUtils.isBlank(watermark) || StringUtils.isBlank(watermarkPosition)) {
            return;
        }

        // 判断宽高
        if (srcwidth > ManipulateConstants.IMAGE_MIN_WIDTH_WATERMARK && srcheight > ManipulateConstants.IMAGE_MIN_HEIGHT_WATERMARK
                && (width == 0 || width > ManipulateConstants.IMAGE_MIN_WIDTH_WATERMARK)
                && (height == 0 || height > ManipulateConstants.IMAGE_MIN_HEIGHT_WATERMARK)) {
            //打水印
            compositeWatermark(destFile, watermark, watermarkPosition);
        }

    }

    //compose with watermark
    private void compositeWatermark(File destFile, String watermark, String watermarkPosition) throws IOException {
        CompositeCmd cpCmd = new CompositeCmd();

        //水印文件
        File wmFile = writeWatermarkFile(destFile.getParent(), watermark);
        if (wmFile == null || !wmFile.exists()) {
            logger.error("write watermark file failed to " + destFile.getParent());
            return;
        }

        //convert watermark position for imagemagick: 如果水印过大
        WatermarkPosition wp = convertWatermarkPosition(watermarkPosition);

        IMOperation wmop = new IMOperation();
        wmop.dissolve((int) ManipulateConstants.WaterMark_Opacity * 100);
        wmop.gravity(wp.name());
        wmop.addImage(); //wm
        wmop.addImage(); //src
        wmop.addImage(); //dest

        try {
            if(logger.isDebugEnabled()){
                logger.debug(wmop.toString());
            }
            cpCmd.run(wmop, wmFile.getPath(), destFile.getPath(), destFile.getPath());
        }
        catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public void resizeFullFilledImage(int srcwidth, int srcheight, File srcFile, IImageSize expectSize, File destFile, String watermark, String watermarkPosition) throws IOException {

        IImageSize realSize = ImageUtils.calcRealDimensionByExpectSize(srcwidth, srcheight, expectSize);

        ConvertCmd cmd = new ConvertCmd();

        // create the operation, add images and operators/options
        IMOperation op = new IMOperation();

        op.strip();
        //op.resample(72, 72); //300 dpi -> 72 dpi ? 貌似有副作用, 如果不指定宽高的时候

        op.quality(ManipulateConstants.Default_Quality * 100);
        op.interlace("Line");  //interlaced PNG or GIF or progressive JPEG
        op.resize(expectSize.getWidth(), expectSize.getHeight(), "^");
        op.gravity("center");
        op.crop(expectSize.getWidth(), expectSize.getHeight(), 0 , 0);
        op.p_repage(); //

        if(needAppendBackground(srcFile, destFile)) {
            op.background("white");
            op.flatten();
        }

        if(realSize.getWidth() < expectSize.getWidth() || realSize.getHeight() < expectSize.getHeight()){
            //又缩小了: 放大了存储尺寸会变大, 而图像质量没提高...所以缩小了
            op.resize(realSize.getWidth(), realSize.getHeight());
        }

        op.addImage(); //src
        op.addImage(); //dest

        // execute the operation
        try {
            if(logger.isDebugEnabled()){
                logger.debug(op.toString());
            }
            cmd.run(op, srcFile.getPath(), destFile.getPath());
        }
        catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }

        //没有水印
        if (StringUtils.isBlank(watermark) || StringUtils.isBlank(watermarkPosition)) {
            return;
        }


        if (realSize.getWidth() > ManipulateConstants.IMAGE_MIN_WIDTH_WATERMARK && realSize.getHeight() > ManipulateConstants.IMAGE_MIN_HEIGHT_WATERMARK ) {
            compositeWatermark(destFile, watermark, watermarkPosition);
        }
    }

    private boolean needAppendBackground(File srcFile, File destFile){
        //gif 可能也要判断
        if(ImageUtils.isFormat(srcFile, "png") && ImageUtils.isFormat(destFile, "jpg")){
            return true;
        }

        return false;
    }

    @Override
    public void transferImage(File srcFile, File destFile) throws IOException {
        ConvertCmd cmd = new ConvertCmd();

        // create the operation, add images and operators/options
        IMOperation op = new IMOperation();
        op.addImage();

        op.strip();
        //op.resample(72, 72);
        op.quality(ManipulateConstants.Default_Quality * 100);

        if(needAppendBackground(srcFile, destFile)) {
            op.background("white");
            op.flatten();
        }

        op.addImage();

        // execute the operation
        try {
            cmd.run(op, srcFile.getPath(), destFile.getPath());
        }
        catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    protected File writeWatermarkFile(String destDir, String watermarkName) {
        File wmFile = new File(destDir, watermarkName);
        if (wmFile.exists()) {
            return wmFile;
        }

        try {
            FileUtils.copyInputStreamToFile(this.getClass().getResourceAsStream("/" + watermarkName), wmFile);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return wmFile;
    }

    protected WatermarkPosition convertWatermarkPosition(String position) {
        switch (position) {
            case "TOP_LEFT":
                return WatermarkPosition.NorthWest;
            case "TOP_CENTER":
                return WatermarkPosition.North;
            case "TOP_RIGHT":
                return WatermarkPosition.NorthEast;
            case "CENTER_LEFT":
                return WatermarkPosition.West;
            case "CENTER":
                return WatermarkPosition.Center;
            case "CENTER_RIGHT":
                return WatermarkPosition.East;
            case "BOTTOM_LEFT":
                return WatermarkPosition.SouthWest;
            case "BOTTOM_CENTER":
                return WatermarkPosition.South;
            case "BOTTOM_RIGHT":
                return WatermarkPosition.SouthEast;
            default:
                return WatermarkPosition.None;
        }
    }

}

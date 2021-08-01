package com.cnscud.xpower.filemanipulate.image.engager;

import com.cnscud.xpower.filemanipulate.ManipulateConstants;
import com.cnscud.xpower.filemanipulate.image.IImageSize;
import com.cnscud.xpower.filemanipulate.image.ImageUtils;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Image Builder by thumbnailator.
 * <p>
 * 对PNG (index color 索引调色板)支持较差.
 *
 * @author Felix Zhang 2016-12-20 21:39
 * @version 1.0.0
 */
public class ThumbnailatorEngager implements ImageEngager {

    private Log logger = LogFactory.getLog(getClass());

    private BufferedImage readWatermark(String watermarkName) {
        BufferedImage watermark = null;
        if (StringUtils.isNotBlank(watermarkName)) {
            watermark = readWaterMarkResource(watermarkName);
        }

        return watermark;
    }

    public BufferedImage readWaterMarkResource(String name) {
        try {
            return ImageIO.read(this.getClass().getResourceAsStream("/" + name));
        }
        catch (Exception e) {
            logger.error("read water mark failed " + name, e);
        }

        return null;
    }

    @Override
    public String getName() {
        return "thumbnailator";
    }

    @Override
    public boolean available() {
        return true;
    }

    public void resizeImage(int srcwidth, int srcheight, File srcFile, IImageSize expectSize, File destFile, String watermark, String watermarkPosition) throws IOException {
        resizeImageNoEnlarge(srcwidth, srcheight, srcFile, expectSize, destFile, watermark, watermarkPosition);
    }

    public void resizeImageNoEnlarge(int srcwidth, int srcheight, File srcFile, IImageSize expectSize, File destFile, String watermark, String watermarkPosition) throws IOException {
        int width = expectSize.getWidth();
        int height = expectSize.getHeight();

        Thumbnails.Builder builder = Thumbnails.of(srcFile);

        if ((srcwidth <= width && srcheight <= height) || (srcwidth <= width && height <= 0) || (width <= 0 && srcheight <= height)) {
            builder.scale(1.0).outputQuality(ManipulateConstants.Default_Quality);
        }
        else if (height <= 0) {
            builder.width(width).outputQuality(ManipulateConstants.Default_Quality);
        }
        else if (width <= 0) {
            builder.height(height).outputQuality(ManipulateConstants.Default_Quality);
        }
        else {
            if(expectSize.isForce()){
                builder.forceSize(width, height).outputQuality(ManipulateConstants.Default_Quality);
            }
            else {
                builder.size(width, height).outputQuality(ManipulateConstants.Default_Quality);
            }
        }

        // 判断宽高, 打水印
        if (srcwidth > ManipulateConstants.IMAGE_MIN_WIDTH_WATERMARK && srcheight > ManipulateConstants.IMAGE_MIN_HEIGHT_WATERMARK
                && (width == 0 || width > ManipulateConstants.IMAGE_MIN_WIDTH_WATERMARK)
                && (height == 0 || height > ManipulateConstants.IMAGE_MIN_HEIGHT_WATERMARK) && watermark != null && watermarkPosition != null) {
            builder.watermark(Positions.valueOf(watermarkPosition), readWatermark(watermark), ManipulateConstants.WaterMark_Opacity);
        }

        builder.toFile(destFile); // 输出到文件
    }

    public void resizeFullFilledImage(int srcwidth, int srcheight, File srcFile, IImageSize expectSize, File destFile, String watermark,
                                      String watermarkPosition) throws IOException {
        IImageSize realSize = ImageUtils.calcRealDimensionByExpectSize(srcwidth, srcheight, expectSize);

        //这个Thumbnails的实现很奇怪.... 此处不会拉大图像, 但是会按比例裁剪
        Thumbnails.Builder builder = Thumbnails.of(srcFile).size(realSize.getWidth(), realSize.getHeight()).crop(Positions.CENTER)
                .outputQuality(ManipulateConstants.Default_Quality);

        // 水印
        if (realSize.getWidth() > ManipulateConstants.IMAGE_MIN_WIDTH_WATERMARK && realSize.getHeight() > ManipulateConstants.IMAGE_MIN_HEIGHT_WATERMARK
                && watermark != null && watermarkPosition != null) {

            builder = Thumbnails.of(builder.asBufferedImage()); // 为了最终获取crop的结果: 会自动旋转
            builder.scale(1.0).outputQuality(ManipulateConstants.Default_Quality);

            builder.watermark(Positions.valueOf(watermarkPosition), readWatermark(watermark), ManipulateConstants.WaterMark_Opacity);
        }

        builder.toFile(destFile);
    }

    /**
     * 转换文件格式.
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @throws IOException 文件不存在时等异常
     */
    public void transferImage(File srcFile, File destFile) throws IOException {
        Thumbnails.of(srcFile).scale(1.0).outputQuality(ManipulateConstants.Default_Quality).toFile(destFile);
    }

}

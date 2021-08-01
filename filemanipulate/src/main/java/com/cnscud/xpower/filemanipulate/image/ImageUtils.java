package com.cnscud.xpower.filemanipulate.image;

import com.cnscud.xpower.filemanipulate.ManipulateConstants;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * some utils.
 * 
 * @author Felix Zhang Date 2013-07-16 11:55
 * @version 1.0.0
 */
public class ImageUtils {

    public static String findTheSize(ImageNameSize[] result, String shortname) {
        if(StringUtils.isBlank(shortname) || result == null){
            return null;
        }

        for (ImageNameSize ins : result) {
            if (shortname.equals(ins.getName())) {
                return ins.getUrl();
            }
        }

        return null;
    }

    public static String findTheSize(ImageProcessData ipd, String expectedSize) {
        if (StringUtils.isBlank(expectedSize) || ipd == null || ipd.getThumbnailImages() == null) {
            return null;
        }
        for (ImageNameSize ins : ipd.getThumbnailImages()) {
            if (expectedSize.equals(ins.getName())) {
                return ins.getUrl();
            }
        }

        return null;
    }

    private String findTheSize(ImageNameSize[] result, IImageSize expectedSize) {

        if(expectedSize == null || result == null){
            return null;
        }

        for (ImageNameSize ins : result) {
            if (expectedSize.getSuffix().equals(ins.getName())) {
                return ins.getUrl();
            }
        }

        return null;
    }


    /**
     * 从字符串参数中解析图像尺寸，支持格式： WxH,WxH,WxH
     * 
     * @param args
     *            图像参数集合，例如：fs:100x200,fb:300x400,fc:200x100
     * @return 从小到大多个图像尺寸的集合
     */
    public static IImageSize[] parseImageSizes(String args) {

        List<IImageSize> imageSizes = new ArrayList<IImageSize>();
        Pattern p1 = Pattern.compile("(\\w+):(\\d+)x(\\d+)");
        int start = 0;
        Matcher m = p1.matcher(args);
        while (m.find(start)) {
            start = m.end();
            if (m.groupCount() == 3) {
                IImageSize imageSize = new DefaultImageSize(m.group(1), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
                imageSizes.add(imageSize);
            }
        }
        // Collections.sort(imageSizes);
        return imageSizes.toArray(new IImageSize[imageSizes.size()]);
    }


    private static int intValue(Double v){
        return (int) Math.ceil(v);
    }

    //此处不处理>2000的问题, 由调用方处理好.
    public static IImageSize calcRealDimensionByExpectSize(int width, int height, IImageSize expectSize) {

        if (expectSize.getSuffix().startsWith("f")) { // 宽高必须都有值
            if (width >= expectSize.getWidth() && height >= expectSize.getHeight()) {
                return new DefaultImageSize("cs", expectSize.getWidth(), expectSize.getHeight());
            }
            else {
                double whratio = (double) expectSize.getWidth() / (double) expectSize.getHeight();
                double widthScaleGuess = (double) expectSize.getWidth() / (double) width;
                double heightScaleGuess = (double) expectSize.getHeight() / (double) height;

                if (widthScaleGuess > heightScaleGuess) {
                    return new DefaultImageSize("cs", width,  intValue ((double) width / whratio));
                }
                else {
                    return new DefaultImageSize("cs",  intValue ((double) height * whratio), height);
                }
            }
        }
        else {
            if (width > expectSize.getWidth() && height > expectSize.getHeight() && expectSize.getWidth() > 0 && expectSize.getHeight() > 0) {
                double widthScaleGuess = (double) expectSize.getWidth() / (double) width;
                double heightScaleGuess = (double) expectSize.getHeight() / (double) height;

                if (widthScaleGuess < heightScaleGuess) {
                    return new DefaultImageSize("cs", expectSize.getWidth(),  intValue ((double) height * widthScaleGuess));
                }
                else {
                    return new DefaultImageSize("cs",  intValue ((double) width * heightScaleGuess), expectSize.getHeight());
                }
            }
            else if (width > expectSize.getWidth() && expectSize.getWidth() > 0) {
                return new DefaultImageSize("cs", expectSize.getWidth(),
                       intValue ((double) expectSize.getWidth() / (double) width * (double) height));
            }
            else if (height > expectSize.getHeight() && expectSize.getHeight() > 0) {
                return new DefaultImageSize("cs", intValue((double) expectSize.getHeight() / (double) height * (double) width),
                        expectSize.getHeight());
            }
            else {
                return new DefaultImageSize("cs", width, height);
            }
        }
    }


    /**
     * 替换URI中后缀的.与@
     * @param uri 文件路径或者地址
     * @return 替换后的URI
     */
    public static String switchSign(String uri) {
        char[] cs = uri.toCharArray();
        for(int i=cs.length-1;i>=0;i--) {
            if(cs[i] == '@') {
                cs[i] = '.';
                break;
            }else if(cs[i] == '.') {
                cs[i] = '@';
                break;
            }
        }
        return new String(cs);
    }

    public static boolean isFormat(File file, String extension){
        return extension.equalsIgnoreCase(FilenameUtils.getExtension(file.getName()));
    }


    //简单版, 取m图像的尺寸
    public static DefaultImageSize pickMediumImageSizeByBigSize(int bwidth, int bheight){
        IImageSize expectSize = ManipulateConstants.PICTURE_MEDIUMSIZE;
        int dwidth = 0;
        int dheight =0;

        if (bwidth >= expectSize.getWidth()) {
            dwidth = expectSize.getWidth();
            dheight = (int) (bheight * expectSize.getWidth() * 1.0 / bwidth);
        }
        else {
            dwidth = bwidth;
            dheight = bheight;
        }

        return new DefaultImageSize("real", dwidth, dheight);
    }



}

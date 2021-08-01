package com.cnscud.xpower.filemanipulate.image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.cnscud.xpower.filemanipulate.image.engager.ImageEngager;
import org.apache.commons.lang.StringUtils;

/**
 * 补全图片尺寸, 原图已有, 指定特定目录.
 * 
 * @author Felix Zhang Date 2013-04-19 15:18
 * @version 1.0.0
 */
public class FillUpImages {

    public static void main(String[] args) throws Exception {

        // 指定目录
        // String dir = "/Users/felixzhang/workapp/tos/filemanipulate/testfile/temp/m13";
        if (args.length != 1) {
            System.err.println("Usage: <imagedir>");
            System.exit(1);
        }
        String dir = args[0];

        File parentDir = new File(dir).getCanonicalFile();

        if (!parentDir.exists()) {
            System.out.println("not find the directory: " + dir);
        }

        int cnt = processDirectory(parentDir);
        System.out.println("PROCESS_COUNT => " + cnt);
    }

    protected static int processDirectory(File dir) {
        int cnt = 0;
        File[] items = dir.listFiles();
        for (File item : items) {
            if (item.isDirectory()) {
                if (!item.getName().startsWith(".")) {
                    cnt += processDirectory(item);
                }
            } else {

                // process image file : original file only
                if (item.getName().contains("_o.")) {
                    cnt += processOneImage(item, items);
                }
            }
        }
        return cnt;
    }

    private static int processOneImage(File sourcefile, File[] items) {
        int cnt = 0;
        System.out.println("process image: " + sourcefile.getPath());

        List<String> existsSizeName = new ArrayList<String>();

        int index = sourcefile.getName().indexOf("_o.");
        String prefix = sourcefile.getName().substring(0, index);

        String sizeInfo = "";
        String extension = "";
        for (File item : items) {
            String name = item.getName();
            if (name.startsWith(prefix) && !name.contains("_o.")) {
                int start = name.lastIndexOf("_");
                int end = name.lastIndexOf(".");
                int sizeStart = name.indexOf("_");
                sizeInfo = name.substring(sizeStart + 1, start);
                extension = name.substring(end + 1);
                existsSizeName.add(name.substring(start + 1, end));
            }
        }

        if (StringUtils.isBlank(sizeInfo) || StringUtils.isBlank(extension)) {
            System.out.println("not find any sized image for " + sourcefile.getPath());
            return cnt;
        }

        IImageInfoChecker checker = ImageInfoUtils.checkImageInfo(sourcefile);
        // 检查不支持的文件格式
        if (checker == null) {
            System.out.println("图像格式错误, 仅支持JPG/JPEG, PNG, GIF格式:" + sourcefile.getPath());

            return cnt;
        }

        File parentDir = sourcefile.getParentFile();

        try {
            // 生成目标缩略图
            for (IImageSize ins : PICTURE_SIZES) {

                if (existsSizeName.contains(ins.getSuffix())) {
                    continue;
                }

                String destFileName = prefix + "_" + sizeInfo + "_" + ins.getSuffix() + "." + extension;
                File destFile = new File(parentDir, destFileName).getCanonicalFile();
                System.out.println("WRITE_FILE => " + destFile);

                // 区分两种格式
                if (ins.getSuffix().startsWith("f")) {
                    imb.resizeFullFilledImage(checker.getWidth(), checker.getHeight(), sourcefile, ins, destFile, null, null);
                } else {
                    // warning: 如果是动画gif, 可能不这么处理
                    imb.resizeImage(checker.getWidth(), checker.getHeight(), sourcefile, ins, destFile, null, null);
                }
                cnt++;
            }
        } catch (Exception e) {
            System.out.println("exception when process file: " + sourcefile.getPath());
            e.printStackTrace();
        }
        return cnt;

    }

    static ImageManipulater im = new ImageManipulater();
    static ImageEngager imb = im.findImageEngager("jpg", "jpg");

    static IImageSize PICTURE_BIGSIZE = new DefaultImageSize("b", 900, 0);
    static IImageSize PICTURE_HEADSIZE = new DefaultImageSize("fb", 630, 420);
    static IImageSize PICTURE_MEDIUMSIZE = new DefaultImageSize("m", 570, 0);
    static IImageSize PICTURE_COVERSIZE = new DefaultImageSize("fc", 315, 210);
    static IImageSize PICTURE_SMALLSIZE = new DefaultImageSize("fs", 150, 100);
    // 原来的
    // IImageSize PICTURE_BIGSIZE = new DefaultImageSize("b", 1000, 0);
    // IImageSize PICTURE_MEDIASIZE = new DefaultImageSize("m", 500, 0);
    // IImageSize PICTURE_SMALLSIZE = new DefaultImageSize("s", 150, 100);

    // 大,中, 头条, 小, 封面
    static IImageSize[] PICTURE_SIZES = new IImageSize[] { PICTURE_BIGSIZE, PICTURE_MEDIUMSIZE, PICTURE_HEADSIZE, PICTURE_SMALLSIZE, PICTURE_COVERSIZE };

    static {
        ImageNameSize.convert(PICTURE_SIZES);
    }
}

package com.cnscud.xpower.filemanipulate.image;

import com.cnscud.xpower.filemanipulate.ManipulateConstants;
import com.cnscud.xpower.filemanipulate.image.engager.ImageEngager;
import com.cnscud.xpower.filemanipulate.image.engager.ThumbnailatorEngager;
import com.cnscud.xpower.filemanipulate.image.engager.ImageMagickEngager;
import com.cnscud.xpower.filemanipulate.image.handler.HandleType;
import com.cnscud.xpower.filemanipulate.image.handler.ImageHandler;
import com.cnscud.xpower.filemanipulate.image.handler.JpegTranHandler;
import com.cnscud.xpower.filemanipulate.image.handler.PngquantHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Image Manipulate.
 * <p/>
 * 此版本都保留原图.
 *
 *
 * @author Felix Zhang Date 2012-10-23 14:32
 * @version 1.0.0
 */
public class ImageManipulater {

    private static Log log = LogFactory.getLog(ImageManipulater.class);

    private ImageEngager defaultEngager = new ThumbnailatorEngager(); //default
    private ImageEngager imEngager = new ImageMagickEngager();


    //图像拦截器, 根据拦截时机和图像类型进行二次处理
    private ImageHandler[] DefaultHandlers = {new JpegTranHandler(), new PngquantHandler()};
    private ImageHandler[] handlers = DefaultHandlers;

    private boolean debug = false;

    public void debug(){
        debug = true;
        for(ImageHandler handler: handlers) {
            handler.setDebug(true);
        }
    }

    public void useHandler(boolean enable){
        if(enable){
            handlers = DefaultHandlers;
        }
        else {
            handlers = new ImageHandler[]{};
        }
    }

    protected ImageEngager findImageEngager(String srcExtension, String destExtension) {
        //注意: png resize不能用 ThumbnailatorEngager , 效果不好
        if(imEngager.available() &&  srcExtension.equalsIgnoreCase("png") ){
            return imEngager;
        }

        return defaultEngager;
    }

    /**
     * 自定义上传图像.
     *
     * @param file               文件
     * @param requiredSizeImages 需要的尺寸
     * @param customType         自定义类别, 请联系管理员定义
     * @param userPrefix         前缀
     * @return 图像结果
     */
    public ResultMessage<ImageProcessData> processCustomizeImageFile(File file, IImageSize[] requiredSizeImages, String customType, String userPrefix,
                                                                     IImageSize minSize, String watermarkConfig) {
        return _processGeneralImageFile(file, ImageNameSize.convert(requiredSizeImages), userPrefix, customType, true, true, minSize, watermarkConfig);
    }

    /**
     * 运维上传图像.
     *
     * @param file               文件
     * @param requiredSizeImages 需要的尺寸
     * @param userPrefix         前缀
     * @return 图像结果
     */
    public ResultMessage<ImageProcessData> processOperationImageFile(File file, IImageSize[] requiredSizeImages, String userPrefix) {
        return _processGeneralImageFile(file, ImageNameSize.convert(requiredSizeImages), userPrefix, ManipulateConstants.ShortName_OperationImage, true, true,
                null, null);
    }

    /**
     * 处理普通图像.
     *
     * @param file               图像文件
     * @param requiredSizeImages 图像尺寸, 名字类似 b, m,s, fs,fm等. 如果名字以f开头, 表明需要full fill crop.
     * @param userPrefix         前缀
     * @return 处理结果, 必须检测!
     */
    public ResultMessage<ImageProcessData> processGeneralImageFile(File file, IImageSize[] requiredSizeImages, String userPrefix, String watermarkConfig) {
        return _processGeneralImageFile(file, ImageNameSize.convert(requiredSizeImages), userPrefix, ManipulateConstants.ShortName_GeneralImage, true, true,
                null, watermarkConfig);
    }

    /**
     * 处理普通图像, 只存为JPG.
     *
     * @param file               图像文件
     * @param requiredSizeImages 图像尺寸, 名字类似 b, m,s, fs,fm等. 如果名字以f开头, 表明需要full fill crop.
     * @param userPrefix         前缀
     * @return 处理结果, 必须检测!
     */
    public ResultMessage<ImageProcessData> processGeneralImageFile(File file, IImageSize[] requiredSizeImages, String userPrefix,
                                                                   String watermarkConfig, boolean keepImageFormat) {
        return _processGeneralImageFile(file, ImageNameSize.convert(requiredSizeImages), userPrefix, ManipulateConstants.ShortName_GeneralImage,
                true, keepImageFormat, null, watermarkConfig);
    }

    /**
     * 处理普通图像.
     *
     * @param file               图像文件
     * @param requiredSizeImages 图像尺寸, 名字类似 b, m,s, fs,fm等. 如果名字以f开头, 表明需要full fill crop.
     * @param userPrefix         前缀
     * @return 处理结果, 必须检测!
     */
    public ResultMessage<ImageProcessData> processGeneralImageFile(File file, IImageSize[] requiredSizeImages, String userPrefix) {
        return _processGeneralImageFile(file, ImageNameSize.convert(requiredSizeImages), userPrefix, ManipulateConstants.ShortName_GeneralImage, true, true,
                null, null);
    }

    /**
     * 处理普通图像, 并检查最小宽高.
     *
     * @param file               图像文件
     * @param requiredSizeImages 图像尺寸, 名字类似 b, m,s, fs,fm等. 如果名字以f开头, 表明需要full fill crop.
     * @param userPrefix         前缀
     * @param minSize            最小宽高, 如果图像小于此尺寸, 则返回错误状态.
     * @return 处理结果, 必须检测!
     */
    public ResultMessage<ImageProcessData> processGeneralImageFile(File file, IImageSize[] requiredSizeImages, String userPrefix, IImageSize minSize) {
        return _processGeneralImageFile(file, ImageNameSize.convert(requiredSizeImages), userPrefix, ManipulateConstants.ShortName_GeneralImage, true, true,
                minSize, null);
    }

    /**
     * 处理普通图像, 并检查最小宽高.
     *
     * @param file               图像文件
     * @param requiredSizeImages 图像尺寸, 名字类似 b, m,s, fs,fm等. 如果名字以f开头, 表明需要full fill crop.
     * @param userPrefix         前缀
     * @param minSize            最小宽高, 如果图像小于此尺寸, 则返回错误状态.
     * @param watermarkConfig    水印配置, 格式 "wm1.png|BOTTOM_RIGHT"
     * @return 处理结果, 必须检测!
     */
    public ResultMessage<ImageProcessData> processGeneralImageFile(File file, IImageSize[] requiredSizeImages, String userPrefix, IImageSize minSize, String watermarkConfig) {
        return _processGeneralImageFile(file, ImageNameSize.convert(requiredSizeImages), userPrefix, ManipulateConstants.ShortName_GeneralImage, true, true,
                minSize, watermarkConfig);
    }
    
    public ResultMessage<ImageProcessData> processGeneralImageFile(ImageParam param){
        return _processGeneralImageFile(param);
    }

    /**
     * 清理处理中生成的文件, 包括原文件.
     *
     * @param file 原文件.
     * @param ipd  处理结果.
     */
    public void cleanTemporaryFiles(File file, ImageProcessData ipd) {

        if (ipd != null && ipd.getParentFileDirectory() != null) {
            FileUtils.deleteQuietly(new File(ipd.getParentFileDirectory()));
        }

        FileUtils.deleteQuietly(file);
    }


    /**
     * 处理一般图像.
     *
     * @param file                   文件
     * @param requiredSizeImages     需要的文件尺寸, 名称信息
     * @param userPrefix             用户前缀信息
     * @param keepImageFormat        是否保持图像格式不变, 还是强制转为jpg
     * @param watermarkConfig        水印配置, 格式 "wm1.png|BOTTOM_RIGHT"
     * @return 处理结果, 无论成功失败, 最终结果保存在ImageProcessData中, 可用于清理现场.
     */
    public ResultMessage<ImageProcessData> processGeneralImageFile(File file, IImageSize[] requiredSizeImages, String userPrefix,
                                                                        boolean keepImageFormat, IImageSize minSize, String watermarkConfig) {
        return _processGeneralImageFile(file, ImageNameSize.convert(requiredSizeImages), userPrefix, ManipulateConstants.ShortName_GeneralImage,
                true, keepImageFormat, minSize, watermarkConfig);
    }

    /**
     * 处理一般图像.
     *
     * @param file                   文件
     * @param requiredSizeImages     需要的文件尺寸, 名称信息
     * @param userPrefix             用户前缀信息
     * @param customType             图像类型, 一般是缩写, 用于目录前缀
     * @param baseWebSourceIfSupport 为了节省流量, 是否生成一个最大2000*2000的伪源文件, 否则使用raw文件作为源文件
     * @param keepImageFormat        是否保持图像格式不变, 还是强制转为jpg
     * @param watermarkConfig        水印配置, 格式 "wm1.png|BOTTOM_RIGHT"
     * @return 处理结果, 无论成功失败, 最终结果保存在ImageProcessData中, 可用于清理现场.
     */
    protected ResultMessage<ImageProcessData> processCustomizeImageFile(File file, ImageNameSize[] requiredSizeImages, String userPrefix, String customType,
                                                                       boolean baseWebSourceIfSupport, boolean keepImageFormat,
                                                                       IImageSize minSize, String watermarkConfig) {
        return _processGeneralImageFile(file, ImageNameSize.convert(requiredSizeImages), userPrefix, customType,
                baseWebSourceIfSupport, keepImageFormat, minSize, watermarkConfig);
    }


    /**
     * 处理一般图像.
     *
     * @param file                   文件
     * @param requiredSizeImages     需要的文件尺寸, 名称信息
     * @param userPrefix             用户前缀信息
     * @param customType             图像类型, 一般是缩写, 用于目录前缀
     * @param baseWebSourceIfSupport 为了节省流量, 是否生成一个最大2000*2000的伪源文件, 否则使用raw文件作为源文件
     * @param keepImageFormat        是否保持图像格式不变, 还是强制转为jpg
     * @param watermarkConfig        水印配置, 格式 "wm1.png|BOTTOM_RIGHT"
     * @return 处理结果, 无论成功失败, 最终结果保存在ImageProcessData中, 可用于清理现场.
     */
    protected ResultMessage<ImageProcessData> _processGeneralImageFile(File file, ImageNameSize[] requiredSizeImages, String userPrefix, String customType,
                                                                       boolean baseWebSourceIfSupport, boolean keepImageFormat, IImageSize minSize, String watermarkConfig) {
        ImageParam param = new ImageParam(file, requiredSizeImages, userPrefix, customType);
        param.setBaseWebSourceIfSupport(baseWebSourceIfSupport).setKeepImageFormat(keepImageFormat).setMinSize(minSize).setWatermarkConfig(watermarkConfig);
        return _processGeneralImageFile(param);
    }
    
    private void copyFile(File src, File dest, boolean keepSourceImage) throws IOException{
        if(keepSourceImage) {
            FileUtils.copyFile(src, dest);
        }else {
            src.renameTo(dest);
        }
    }
    protected ResultMessage<ImageProcessData> _processGeneralImageFile(ImageParam param){
        final File file = param.file;
        final IImageSize[] requiredSizeImages = param.requiredSizeImages;
        final String userPrefix = param.userPrefix;
        final String customType = param.customType;
        final boolean baseWebSourceIfSupport = param.baseWebSourceIfSupport;
        boolean keepImageFormat = param.keepImageFormat;
        final IImageSize minSize = param.minSize;
        final String watermarkConfig = param.watermarkConfig;
        
        ResultMessage<ImageProcessData> rm = new ResultMessage<ImageProcessData>();
        rm.setSuccess(true);

        ImageProcessData ipd = new ImageProcessData();

        try {
            if(!file.exists()) {
                rm.setSuccess(false);
                rm.setCode(ManipulateConstants.IMAGE_BADFORMAT);
                rm.setMessage("图像文件不存在: "+file);
                return rm;
            }

            //里面内置了一个JPG格式 按方向旋转的功能: 暂时不摘出来
            IImageInfoChecker checker = ImageInfoUtils.checkImageInfo(file);

            // 检查不支持的文件格式
            if (checker == null) {
                rm.setSuccess(false);
                rm.setCode(ManipulateConstants.IMAGE_BADFORMAT);
                rm.setMessage("图像格式错误, 仅支持JPG/JPEG, PNG, GIF格式.");

                return rm;
            }

            // 检查最小尺寸
            if (minSize != null) {
                if ((checker.getWidth() < minSize.getWidth()) || (checker.getHeight() < minSize.getHeight())) {
                    rm.setSuccess(false);
                    rm.setCode(ManipulateConstants.IMAGE_TOOSMALL);
                    rm.setMessage("图像最小宽高为: " + minSize.getWidth() + "x" + minSize.getHeight());

                    return rm;
                }
            }

            //boolean needCropSize = false;
            IImageSize signedSize = new ImageNameSize("base", ManipulateConstants.IMAGE_BASE_WIDTH, ManipulateConstants.IMAGE_BASE_HEIGHT);
            IImageSize bigSize = null;
            IImageSize fbigSize = null;
            boolean allIsFullFilled = true;
            for (IImageSize ins : requiredSizeImages) {
                if (ins.getSuffix().startsWith("f") && ins.getWidth() > 0 && ins.getHeight() > 0) {
                    //needCropSize = true;

                    //改为保持原格式设置, 不因为这个改变 keepImageFormat
                    //keepImageFormat = false; // 如果需要裁剪, 则无法保持动画 (暂定, 也许某种情况下需要透明背景.....)
                }
                else if (ins.getSuffix().startsWith("f") && (ins.getWidth() <= 0 || ins.getHeight() <= 0)) {
                    rm.setSuccess(false);
                    rm.setCode(ManipulateConstants.IMAGE_BADPARA);
                    rm.setMessage("参数错误, 请联系管理员");

                    return rm;
                }

                if (!ins.getSuffix().startsWith("f")) {
                    allIsFullFilled = false;
                }

                // 避免同时出现!
                if (ins.getSuffix().endsWith("b")&&!ins.getSuffix().startsWith("f")) { // 约定
                    bigSize = ins;
                   // signedSize = ins;
                }

                if (ins.getSuffix().startsWith("f") && ins.getSuffix().endsWith("b") && bigSize == null) { // 约定
                    fbigSize = ins;
                }

                // 全部都是f开头, 而且有fb
                if (allIsFullFilled && fbigSize != null) {
                    //signedSize = fbigSize;
                }
            }
            signedSize = bigSize != null ? bigSize : fbigSize != null ? fbigSize : signedSize;
            
            String oldExtension = getFileSuffixByFormat(checker); // 根据实际文件格式设置后缀

            String extension; // 最终缩略图的后缀
            String rawExtension; // 保存的原图后缀
            if (StringUtils.isNotBlank(oldExtension)) {
                oldExtension = oldExtension.toLowerCase();
            }

            rawExtension = oldExtension;
            //仅支持jpg, png, gif, 如果不是就都改为jpg
            if (oldExtension == null || oldExtension.equals("?") || !ArrayUtils.contains(ManipulateConstants.SupportImageFormats, oldExtension)) {
                rawExtension = ManipulateConstants.DefaultImageFormat;
            }

            if (keepImageFormat) {
                extension = rawExtension;
            }
            else {
                extension = ManipulateConstants.DefaultImageFormat;
            }

            // 设置类型的简称
            ipd.setShortType(customType);

            String imageId = pickNewImageId();
            String imageDirectory = pickImageDirectoryName(customType, imageId, userPrefix);// 本地临时目录

            File swapFileDirectory = new File(file.getParent(), imageDirectory);

            if (!swapFileDirectory.exists()) {
                swapFileDirectory.mkdirs();
            }

            String originalImageFileName;
            File originalFile = null;

            String rawSourceFileName = null; // only jpg in SupportBaseImageFormats[]
            String webSourceFileName = null;

            IImageSize dimension = new DefaultImageSize("ss", checker.getWidth() , checker.getHeight());

            //@拦截器 原图拦截器
            processByHandlers(HandleType.SOURCEIMAGE, file.getParent(), file.getName(), oldExtension);

            long size = file.length(); //拦截器有可能改变size

            // if is jpg
            if (ArrayUtils.contains(ManipulateConstants.SupportImageFormats, oldExtension)
                    && ArrayUtils.contains(ManipulateConstants.SupportBaseImageFormats, oldExtension)) {

                if (baseWebSourceIfSupport) {
                    originalImageFileName = pickOriginalImageFileName(imageId, oldExtension, userPrefix, ManipulateConstants.IMAGE_ORIGINAL_NAME);
                }
                else {
                    originalImageFileName = pickOriginalImageFileName(imageId, oldExtension, userPrefix, ManipulateConstants.IMAGE_SOURCE_NAME);
                }

                originalFile = new File(swapFileDirectory, originalImageFileName);
                copyFile(file, originalFile, param.isKeepSourceImage());

                if (baseWebSourceIfSupport) {
                    rawSourceFileName = originalImageFileName;

                    webSourceFileName = pickOriginalImageFileName(imageId, rawExtension, userPrefix, ManipulateConstants.IMAGE_SOURCE_NAME);

                    // 如果原图尺寸小于预定尺寸, 直接copy raw
                    if (checker.getWidth() <= ManipulateConstants.IMAGE_BASE_WIDTH && checker.getHeight() <= ManipulateConstants.IMAGE_BASE_HEIGHT) {
                        FileUtils.copyFile(new File(swapFileDirectory, rawSourceFileName), new File(swapFileDirectory, webSourceFileName));
                    }
                    else {
                        File webSourceFile = new File(swapFileDirectory, webSourceFileName);

                        findImageEngager(rawExtension, rawExtension).resizeImage(dimension.getWidth(), dimension.getHeight(), originalFile,
                                ManipulateConstants.BASE_SOURCE_SIZE, webSourceFile, null, null);

                        // 重新计算dimension, size
                        size = webSourceFile.length();
                        dimension = calcDimension(checker.getWidth(), checker.getHeight());
                    }
                }
                else {
                    webSourceFileName = originalImageFileName;
                }

            }
            // 例如gif
            else if (ArrayUtils.contains(ManipulateConstants.SupportImageFormats, oldExtension)
                    && !ArrayUtils.contains(ManipulateConstants.SupportBaseImageFormats, oldExtension)) {
                originalImageFileName = pickOriginalImageFileName(imageId, oldExtension, userPrefix, ManipulateConstants.IMAGE_SOURCE_NAME);

                originalFile = new File(swapFileDirectory, originalImageFileName);
                copyFile(file, originalFile, param.isKeepSourceImage());

                webSourceFileName = originalImageFileName; // 只有赋值才会保存到结果里
            }
            // 例如bmp, pcx等, 目前一般都不支持, 所以...
            else if (!ArrayUtils.contains(ManipulateConstants.SupportImageFormats, oldExtension)) {

                originalImageFileName = pickOriginalImageFileName(imageId, oldExtension, userPrefix, "raw");

                originalFile = new File(swapFileDirectory, originalImageFileName);
                copyFile(file, originalFile, param.isKeepSourceImage());

                if (baseWebSourceIfSupport) {
                    rawSourceFileName = pickOriginalImageFileName(imageId, rawExtension, userPrefix, ManipulateConstants.IMAGE_ORIGINAL_NAME);
                    File rawSourceFile = new File(swapFileDirectory, rawSourceFileName);
                    findImageEngager(oldExtension, rawExtension).transferImage(originalFile, rawSourceFile); //转换格式, 设置图像质量

                    webSourceFileName = pickOriginalImageFileName(imageId, rawExtension, userPrefix, ManipulateConstants.IMAGE_SOURCE_NAME);

                    // 如果原图尺寸小: 直接copy raw
                    if (checker.getWidth() <= ManipulateConstants.IMAGE_BASE_WIDTH && checker.getHeight() <= ManipulateConstants.IMAGE_BASE_HEIGHT) {
                        FileUtils.copyFile(new File(swapFileDirectory, rawSourceFileName), new File(swapFileDirectory, webSourceFileName));
                    }
                    else {
                        findImageEngager(rawExtension, rawExtension).resizeImage(dimension.getWidth(), dimension.getHeight(),rawSourceFile,
                                ManipulateConstants.BASE_SOURCE_SIZE, new File(swapFileDirectory, webSourceFileName), null, null);

                        // 重新计算dimension, size
                        dimension = calcDimension(checker.getWidth(), checker.getHeight());
                    }
                }
                else {
                    webSourceFileName = pickOriginalImageFileName(imageId, rawExtension, userPrefix, ManipulateConstants.IMAGE_SOURCE_NAME);
                    findImageEngager(oldExtension, rawExtension).transferImage(originalFile, new File(swapFileDirectory, webSourceFileName));
                }

                // 重新获取文件存储大小
                File webSourceFile = new File(swapFileDirectory, webSourceFileName);
                size = webSourceFile.length();

            }
            else {
                log.error(" what happend? when process image file: " + file.getName());

                rm.setSuccess(false);
                rm.setCode(ManipulateConstants.IMAGE_ERROR);
                rm.setMessage("处理图像发生异常");

                rm.setData(ipd);

                return rm;
            }

            // set dimension
            ipd.setDimension(dimension.getWidth() + "x" + dimension.getHeight());
            ipd.setSize(size);

            // 中间结果也要保存, 为了清理现场
            ipd.setImageId(imageId);
            ipd.setParentFileDirectory(swapFileDirectory.getPath());

            // 保存websource, rawSource文件信息
            if (webSourceFileName != null) {
                ipd.setSourceImageName(webSourceFileName);
            }

            if (rawSourceFileName != null) {
                ipd.setRawSourceImageName(rawSourceFileName);
            }

            ImageNameSize[] destSizeImages = cloneImageNameSizeArray(requiredSizeImages);

            // 计算裁剪后的图
            String sizeInfo = "";
            //此处之后的文件的宽高必须是正确方向的,否则会计算出错误的名字
            IImageSize realSignedSize = ImageUtils.calcRealDimensionByExpectSize(dimension.getWidth(), dimension.getHeight(), signedSize);
            if (signedSize != null) {
                sizeInfo = realSignedSize.getWidth() + "x" + realSignedSize.getHeight();
            }

            // 水印设置
            String watermark = null;
            String watermarkPosition = ManipulateConstants.WaterMark_Postion_Default;
            if (StringUtils.isNotBlank(watermarkConfig)) {
                String[] wmParts = watermarkConfig.split("\\|", 2);

                if (StringUtils.isNotBlank(wmParts[0])) {
                    watermark = wmParts[0];
                }
                if (StringUtils.isNotBlank(wmParts[1])) {
                    watermarkPosition = wmParts[1];
                }
            }

            //缩略图转换的基础图:  ( 保持原图?... png -> jpg 再搞个中间图??   --- 暂时不搞...)
            File thumbnailSourceFile = new File(swapFileDirectory, webSourceFileName);
            int tsrcWidth = dimension.getWidth();
            int tsrcHeight = dimension.getHeight();

            // 生成目标缩略图
            for (ImageNameSize ins : destSizeImages) {
                String destFileName = pickThumbnailImageFileName(imageId, extension, userPrefix, ins.getName(), sizeInfo);

                // 区分两种格式
                if (ins.getSuffix().startsWith("f")) {
                    findImageEngager(rawExtension, extension).resizeFullFilledImage(tsrcWidth, tsrcHeight, thumbnailSourceFile, ins,
                            new File(swapFileDirectory, destFileName), watermark, watermarkPosition);
                }
                else {
                    // warning: 如果是动画gif, 可能不这么处理
                    findImageEngager(rawExtension, extension).resizeImage(tsrcWidth, tsrcHeight,thumbnailSourceFile, ins,
                            new File(swapFileDirectory, destFileName), watermark, watermarkPosition);
                }

                // 此缩略图的文件名
                ins.setFilename(destFileName);
            }

            ipd.setThumbnailImages(destSizeImages);

            //调用拦截器 对生成的所有图像进行最后一次优化
            processByHandlers(HandleType.ALLIMAGE, ipd);

            rm.setSuccess(true);
        }
        catch (IOException e) {
            log.error("resize image file error " + file.getName(), e);

            rm.setSuccess(false);
            rm.setCode(ManipulateConstants.IMAGE_ERROR);
            rm.setMessage("处理图像发生异常");
        }

        rm.setData(ipd);
        return rm;
    }

    private void processByHandlers(HandleType handleType, String parentDir, String fileName, String oldExtension) {
        for(ImageHandler handler: handlers){
            if(handler.accept(oldExtension) && handleType.equals(handler.getHandleType()) ){
                handler.execute(parentDir, fileName);
            }
        }
    }

    private void processByHandlers(HandleType handleType, ImageProcessData ipd) {
        String extension = FilenameUtils.getExtension(ipd.getSourceImageName());
        processByHandlers(handleType, ipd.getParentFileDirectory(), ipd.getSourceImageName(), extension);

        if(ipd.getThumbnailImages().length >0 ) {

            ImageNameSize fins = ipd.getThumbnailImages()[0];
            String aextension = FilenameUtils.getExtension(fins.getFilename());

            String[] fileNames = new String[ipd.getThumbnailImages().length];
            int i=0;
            for (ImageNameSize ins : ipd.getThumbnailImages()) {
                fileNames[i++] = ins.getFilename();
            }

            for(ImageHandler handler: handlers){
                if(handler.accept(aextension) && handleType.equals(handler.getHandleType()) ){
                    handler.execute(ipd.getParentFileDirectory(), fileNames);
                }
            }
        }
    }

    private static int intValue(Double v){
        return (int) Math.ceil(v);
    }

    protected static IImageSize calcDimension(int width, int height) {
        if (width > ManipulateConstants.IMAGE_BASE_WIDTH && height > ManipulateConstants.IMAGE_BASE_HEIGHT) {
            double widthScaleGuess = (double) ManipulateConstants.IMAGE_BASE_WIDTH / (double) width;
            double heightScaleGuess = (double) ManipulateConstants.IMAGE_BASE_HEIGHT / (double) height;

            if (widthScaleGuess < heightScaleGuess) {
                return new DefaultImageSize("ss", ManipulateConstants.IMAGE_BASE_WIDTH , intValue((double) height * widthScaleGuess));
            }
            else {
                return new DefaultImageSize("ss", intValue ((double) width * heightScaleGuess) , ManipulateConstants.IMAGE_BASE_HEIGHT);
            }
        }
        else if (width > ManipulateConstants.IMAGE_BASE_WIDTH) {
            return new DefaultImageSize("ss", ManipulateConstants.IMAGE_BASE_WIDTH ,
                    intValue((double) ManipulateConstants.IMAGE_BASE_WIDTH / (double) width * (double) height));
        }
        else if (height > ManipulateConstants.IMAGE_BASE_HEIGHT) {
            return new DefaultImageSize("ss", intValue((double) ManipulateConstants.IMAGE_BASE_HEIGHT / (double) height * (double) width),
                      ManipulateConstants.IMAGE_BASE_HEIGHT);
        }
        else {
            return new DefaultImageSize("ss", width ,  height);
        }
    }



    /**
     * 检查并处理头像文件.
     *
     * @param file               头像文件
     * @param requiredSizeImages 需要的尺寸
     * @param userPrefix         用户前缀, 一般是id
     * @return 处理结果, 无论成功失败, 最终结果保存在ImageProcessData中, 可用于清理现场.
     */
    public ResultMessage<ImageProcessData> processAvatarRawFile(File file, IImageSize[] requiredSizeImages, String userPrefix) {
        return _processAvatarRawFile(file, ImageNameSize.convert(requiredSizeImages), userPrefix);
    }

    /**
     * 处理头像文件.
     *
     * @param file               头像文件
     * @param requiredSizeImages 需要的尺寸
     * @param userPrefix         用户前缀, 一般是id
     * @return 处理结果, 无论成功失败, 最终结果保存在ImageProcessData中, 可用于清理现场.
     */
    protected ResultMessage<ImageProcessData> _processAvatarRawFile(File file, ImageNameSize[] requiredSizeImages, String userPrefix) {
        // 如果头像处理方式和普通图像不同, 则需要增加逻辑
        return _processGeneralImageFile(file, requiredSizeImages, userPrefix, ManipulateConstants.ShortName_Avatar, false,
                ManipulateConstants.KeepAvatarImageFormat, null, null);

    }

    private String getFileSuffixByFormat(IImageInfoChecker checker) {
        String format = checker.getFormatName();
        format = format.toLowerCase();
        if ("jpeg".equals(format)) {
            format = "jpg";
        }

        if ("?".equals(format)) {
            format = "no";
        }

        return format;
    }

    private ImageNameSize[] cloneImageNameSizeArray(IImageSize[] src) {
        ImageNameSize[] dest = new ImageNameSize[src.length];
        for (int i = 0; i < src.length; i++) {
            if(src[i] instanceof ImageNameSize) {
                ImageNameSize to = ((ImageNameSize) src[i]).clone();
                dest[i] = to;
            }else {
                ImageNameSize to = ImageNameSize.n(src[i].getSuffix(), src[i].getWidth(), src[i].getHeight());
                dest[i] = to;
            }
        }

        return dest;
    }

    private String pickNewImageId() {
        return RandomStringUtils.randomAlphabetic(3).toLowerCase(); // 随机id
    }

    // 规则集中
    private String pickThumbnailImageFileName(String imageId, String extension, String userPrefix, String sizeName, String sizeInfo) {
        if (sizeInfo != null && sizeInfo.length() > 0) {
            return (userPrefix == null ? imageId : (userPrefix + imageId)) + "_" + sizeInfo + "_" + sizeName + "." + extension;
        }
        else {
            return (userPrefix == null ? imageId : (userPrefix + imageId)) + "_" + sizeName + "." + extension;
        }
    }

    private String pickOriginalImageFileName(String imageId, String extension, String userPrefix, String showName) {
        return (userPrefix == null ? imageId : (userPrefix + imageId)) + "_" + showName + "." + extension;
    }

    // 本地临时目录
    private String pickImageDirectoryName(String typeShortName, String imageId, String userPrefix) {
        return typeShortName + DateFormatUtils.format(new Date(), "yyyyMMddHHmm") + (userPrefix == null ? imageId : (userPrefix + imageId));
    }

    public ResultMessage<ImageProcessData> processAvatarRawFile(String fileDirectory, String filename, ImageNameSize[] requiredSizes, String userPrefix) {
        return processAvatarRawFile(new File(fileDirectory, filename), requiredSizes, userPrefix);
    }

}

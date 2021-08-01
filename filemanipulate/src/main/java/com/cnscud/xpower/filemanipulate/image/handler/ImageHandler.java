package com.cnscud.xpower.filemanipulate.image.handler;


import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.im4java.core.ImageCommand;
import org.im4java.core.Operation;

import java.io.File;

/**
 * 图像处理器, 例如优化/采用不同的编码方式等, 用于减少图像的尺寸.
 *
 * @author Felix Zhang 2016-12-21 16:59
 * @version 1.0.0
 */
public abstract class ImageHandler {

    protected Log log = LogFactory.getLog(this.getClass());
    protected Log mailLog = LogFactory.getLog("MAILWARNING");

    protected boolean debug = false;

    public abstract String getName();

    public abstract String getShortName();

    /**
     * @param extension 文件后缀(真实格式对应的后缀)
     * @return 如果是, 返回true
     */
    public abstract boolean accept(String extension);

    public abstract boolean available();

    /**
     * 处理的场景.
     *
     * @return 场景类型
     */
    public abstract HandleType getHandleType();

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * 要处理的图像, 文件名不能变.
     * <p>
     * 内部实现: 如果用临时文件, 记得删除.
     *
     * @param imgFileName 源文件
     */
    public void execute(String parentDir, String imgFileName) {
        if(available()) {
            execute(parentDir, new String[]{imgFileName});
        }
    }


    /**
     * 要处理的图像, 文件名不能变.
     * <p>
     * 内部实现: 如果用临时文件, 记得删除.
     *
     * @param imgFileNames 多个源文件
     */
    public void execute(String parentDir, String[] imgFileNames) {
        if(!available()) {
            return;
        }


        ImageCommand cmd = getHandlerCmd();
        Operation op = getOperation();

        String currentFile = "";

        try {

            for (String imgFileName : imgFileNames) {
                File imgFile = new File(parentDir, imgFileName);

                currentFile = imgFile.getPath();

                String swapFilename = getShortName() + "_" + imgFileName;

                File swapFile = new File(parentDir, swapFilename);
                FileUtils.deleteQuietly(swapFile);

                //运行
                cmd.run(op, swapFile.getPath(), imgFile.getPath());

                //覆盖
                if (swapFile.exists() && swapFile.length() < imgFile.length()) {
                    if (debug) {
                        String bakFilename = getShortName() + "bak_" + imgFileName;
                        FileUtils.copyFile(imgFile, new File(parentDir, bakFilename));
                    }

                    FileUtils.copyFile(swapFile, imgFile);
                    FileUtils.deleteQuietly(swapFile);
                }
                else {
                    log.debug("not reduce file size, nothing replaced");
                    FileUtils.deleteQuietly(swapFile);
                }
            }
        }
        catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Cannot run program")) {
                mailLog.error(getName() + " image handler failed for image " + currentFile, e);
            }

            log.error(getName() + " image handler failed for image " + currentFile, e);
        }


    }

    /**
     * destfile, srcfile 的占位符顺序. 可复用.
     *
     * @return Operation
     */
    protected abstract Operation getOperation();

    protected abstract ImageCommand getHandlerCmd();
}

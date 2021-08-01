package com.cnscud.xpower.filemanipulate.store;

import com.cnscud.xpower.filemanipulate.image.FileProcessData;
import com.cnscud.xpower.filemanipulate.image.ImageProcessData;
import com.cnscud.xpower.filemanipulate.image.ResultMessage;
import com.cnscud.xpower.filemanipulate.store.DeployServerItem.ServerType;

import java.io.Closeable;
import java.util.List;

/**
 * Deploy File Worker.
 *
 * @author Felix Zhang  Date 2012-10-24 10:25
 * @version 1.0.0
 */
public interface DeployFileWorker extends Closeable{

    default ResultMessage<ImageProcessData> deployImages(ImageProcessData ipd){
        return deployImages(ipd, ServerType.NORMAL);
    }
    /**
     * 发布图像文件到目标服务器.
     *
     * @param ipd 图像信息, 一般是上一步的结果, 应该包括源文件信息, 几种尺寸的文件信息
     * @return 处理结果, 需要检查是否成功, 成功后才有data, 否则可能没有.
     */
    ResultMessage<ImageProcessData> deployImages(ImageProcessData ipd, ServerType serverType);

    /**
     * 发布普通文件到服务器.
     * @param shortType 类型简称.
     * @param localDirectory 本地目录
     * @param filename 文件名, 不包括路径
     * @return 处理结果,需要检查是否成功, 成功后才有data, 否则可能没有.
     */
    ResultMessage<String> deployFile(String shortType, String localDirectory, String filename);

    /**
     * 发布普通文件到服务器.
     * @param shortType 类型简称.
     * @param localDirectory 本地目录
     * @param filename 文件名, 不包括路径
     * @return 处理结果,需要检查是否成功, 成功后才有data, 否则可能没有.
     */
    ResultMessage<String> deployFile(String shortType, String localDirectory,String remoteDirectory, String filename);

    /**
     *
     * @param shortType
     * @param localDirectory
     * @param remoteDirectory
     * @param filename
     * @param serverType
     * @return
     */
    ResultMessage<String> deployFile(String shortType, String localDirectory, String remoteDirectory, String filename, ServerType serverType);

    ResultMessage<FileProcessData> deployFile2(String shortType, String localDirectory, String remoteDirectory, String filename, ServerType serverType);

    /**
     * 删除服务器上的文件.
     *
     * @param fileUrls 文件网址.
     * @return 有可能成功, 失败也不奇怪. 基于CDN原理, 你的文件可能不会被销毁.
     */
    ResultMessage deleteFile(List<String> fileUrls);
    
    String getCallback(String biz);
}

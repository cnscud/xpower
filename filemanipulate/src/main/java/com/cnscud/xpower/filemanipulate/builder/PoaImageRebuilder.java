package com.cnscud.xpower.filemanipulate.builder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.cnscud.xpower.filemanipulate.ManipulateConstants;
import com.cnscud.xpower.filemanipulate.image.IImageSize;
import com.cnscud.xpower.filemanipulate.image.ImageNameSize;
import com.cnscud.xpower.filemanipulate.image.ImageParam;
import com.cnscud.xpower.filemanipulate.image.ResultMessage;
import org.apache.commons.io.FileUtils;

import com.cnscud.xpower.filemanipulate.image.ImageManipulater;
import com.cnscud.xpower.filemanipulate.image.ImageProcessData;

/**
 * 本程序的目的替换POA图片的水印（保留原始图片地址）
 * @author adyliu (imxylz@gmail.com)
 * @since 2016年12月22日
 */
public class PoaImageRebuilder implements AutoCloseable{

    final Map<String, DomainHost> domainHosts = DomainHost.avaiable();
    final ImageManipulater imageManipulater = new ImageManipulater();
    final ExecutorService executorService;

    public PoaImageRebuilder(int nThreads) {
        executorService = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, //
                new ArrayBlockingQueue<Runnable>(nThreads * 2), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @FunctionalInterface
    static interface ResultCallback {
        void callback(String imageUrl, String result);
    }

    void log(String msg, Object... args) {
        System.out.printf(msg, args);
    }

    /**
     * 检测URL对应的文件，并执行替换
     * 
     * @param imageUrl
     *            http://test1.sjbly.cn/m16/1215/1950/6808fpw_630x420_fs.jpg
     * @return
     */
    boolean checkAndRebuild(String imageUrl, String hostname, ResultCallback callback) throws Exception {
        final URL url = new URL(imageUrl);
        final String path = url.getPath();
        final String domain = url.getHost();// test1.sjbly.cn
        final DomainHost dh = domainHosts.get(domain);
        if (dh == null || !dh.host.equals(hostname)) {// 不是在这台机器上执行的，忽略（因为肯定没有文件啊）
            return false;
        }
        final File localFile = new File(dh.ftpRootPath, path);// /opt/ftpupload/f1/m16/1215/6808fpw_630x420_fs.jpg
        final File originFile = findOriginFile(dh.ftpRootPath, ManipulateConstants.replace(path, "o")); // /opt/ftpupload/f1/m16/1215/1950/6808fpw_o.jpg
        if (originFile == null) {
            callback.callback(imageUrl, "没有原始图片");
            return false;
        }
        File tmpFile = new File("/tmp", originFile.getName());
        FileUtils.copyFile(originFile, tmpFile);

        IImageSize[] imageSizes =  ManipulateConstants.GENERAL_PICTURE_SIZES;
        ImageParam impageParam = new ImageParam(tmpFile, imageSizes, "00", "fixed");
        impageParam.withDefaultWatermarkConfig().setBaseWebSourceIfSupport(false);
        ResultMessage<ImageProcessData> ret = null;
        try {
            ret = imageManipulater.processGeneralImageFile(impageParam);
            if (!ret.isSuccess()) {
                callback.callback(imageUrl, "错误-原因未知" + ret.getCode() + "," + ret.getMessage());
                return false;
            } else {
                for (ImageNameSize ins : ret.getData().getThumbnailImages()) {
                    File thumbnailFile = new File(ret.getData().getParentFileDirectory(), ins.getFilename());
                    File toFile = new File(ManipulateConstants.replace(localFile.getPath(), ins));
                    log("copy %s -> %s\n", thumbnailFile, toFile);
                    FileUtils.copyFile(thumbnailFile, toFile);
                }
                callback.callback(imageUrl, "成功");
                return true;
            }

        } catch (Exception ex) {
            log(imageUrl + ": " + ex.getMessage());
            ex.printStackTrace();
            callback.callback(imageUrl, "错误-生成图片失败");
        } finally {
            if (ret != null) {
                imageManipulater.cleanTemporaryFiles(tmpFile, ret.getData());
            }
        }
        return false;
    }

    /** 查找原图 */
    private File findOriginFile(String dir, String path) {
        File srcFile = new File(dir, path);
        if (!srcFile.exists()) {
            for (String suffix : new String[] { ".png", ".gif", ".jpg" }) {
                srcFile = new File(dir, path.replaceAll("\\.[^.]+$", suffix));
                if (srcFile.exists()) {
                    break;
                }
            }
        }
        return srcFile.exists() ? srcFile : null;
    }
    /**
     * 替换一个文件列表（filelist中的每一个url对于的机器名称必须和hostname匹配，这是一个校验机制）
     * @param filelist 每一行是一个图片文件
     * @param hostname 要执行的机器名称 (如果filelist中某个URL对于的机器名称不是当前hostname，则跳过此URL）
     * @throws Exception
     * @see {@link DomainHost}
     */
    void run(File filelist, String hostname) throws Exception {
        final File progressFile = new File(filelist.getName() + ".progress");
        final Set<String> progress = new HashSet<>(20000);
        if (progressFile.exists()) {
            FileUtils.readLines(progressFile, "UTF-8").forEach(x -> {
                progress.add(x.trim().split(" ")[0]);
            });
        }
        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(progressFile, true), "UTF-8"))) {

            final ResultCallback callback = (imageUrl, ret) -> {
                writer.printf("%s %s\n", imageUrl, ret);
            };
            //
            List<String> urls = FileUtils.readLines(filelist, "UTF-8");
            for (int i = 0; i < urls.size(); i++) {
                String url = urls.get(i);
                if (!progress.contains(url)) {
                    executorService.submit(()->{
                        try {
                            checkAndRebuild(url, hostname, callback);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    
                }
                log("progress %d of %d\n", i + 1, urls.size());
            }
        }
    }
    
    @Override
    public void close() throws Exception {
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
    }

    public static void main(String[] args) throws Exception {
        try (PoaImageRebuilder rebuilder = new PoaImageRebuilder(5)) {// 5个线程并发执行
            rebuilder.run(new File(args[0]), args[1]);
        }
    }

}

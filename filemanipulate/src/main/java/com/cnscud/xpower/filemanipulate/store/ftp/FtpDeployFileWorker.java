package com.cnscud.xpower.filemanipulate.store.ftp;

import com.github.zkclient.IZkDataListener;
import com.cnscud.xpower.filemanipulate.ManipulateConstants;
import com.cnscud.xpower.filemanipulate.image.FileProcessData;
import com.cnscud.xpower.filemanipulate.image.ImageNameSize;
import com.cnscud.xpower.filemanipulate.image.ImageUtils;
import com.cnscud.xpower.filemanipulate.image.ResultMessage;
import com.cnscud.xpower.filemanipulate.image.ImageProcessData;
import com.cnscud.xpower.filemanipulate.store.DeployException;
import com.cnscud.xpower.filemanipulate.store.DeployFileUtils;
import com.cnscud.xpower.filemanipulate.store.DeployFileWorker;
import com.cnscud.xpower.filemanipulate.store.DeployServerItem;
import com.cnscud.xpower.filemanipulate.store.DeployServerItem.ServerType;
import com.cnscud.xpower.configcenter.ConfigCenter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Ftp Store File Worker.
 * 
 * @author Felix Zhang Date 2012-10-24 14:26
 * @version 1.0.0
 */
public class FtpDeployFileWorker implements DeployFileWorker, IZkDataListener {

    private static final String DEFAULT_FTP_SERVER_PATH = "/xpower/ftpserver/avatar"; //如果服务器磁盘满了, 把状态改为1即可,这样依然可以删文件.

    final Logger log = LoggerFactory.getLogger(getClass());

    

    private FtpStoreSetting storeSetting;
    private final AtomicBoolean subscribeDataChange = new AtomicBoolean(false);
    private String serverPath = DEFAULT_FTP_SERVER_PATH;
    private final String poolId = UUID.randomUUID().toString().substring(0, 8);

    private ConcurrentMap<ServerType, GenericObjectPool<IFtpClient>> ftpPools = new ConcurrentHashMap<>();

    protected GenericObjectPool<IFtpClient> setupFtpPool(ServerType serverType) {
        GenericObjectPool<IFtpClient> ftpPool = new GenericObjectPool<IFtpClient>(new FtpClientFactory(getStoreSetting(),serverType,poolId));
        ftpPool.setMaxActive(100);
        ftpPool.setMinIdle(1);
        ftpPool.setMaxIdle(10);
        ftpPool.setMaxWait(10000L);
        ftpPool.setTestOnReturn(true);
        ftpPool.setTestWhileIdle(true);
        ftpPool.setMinEvictableIdleTimeMillis(10 * 60 * 1000L);//闲置连接被踢时间，默认10分钟
        ftpPool.setNumTestsPerEvictionRun(10);
        ftpPool.setTimeBetweenEvictionRunsMillis(30 * 1000L);
        ftpPool.setLifo(false);
        return ftpPool;
    }

    private GenericObjectPool<IFtpClient> _getFtpPool(ServerType serverType){
        return ftpPools.computeIfAbsent(serverType, this::setupFtpPool);
    }

    public ResultMessage<ImageProcessData> deployImages(ImageProcessData ipd, ServerType serverType) {
        final GenericObjectPool<IFtpClient> ftpPool = _getFtpPool(serverType);
        int retry = Math.max(1, getStoreSetting().getRetryTimes());
        while (retry-- > 0) {
            IFtpClient client = null;
            try {
                client = ftpPool.borrowObject();
                ResultMessage<ImageProcessData> rm = new ResultMessage<ImageProcessData>(false, ManipulateConstants.FTP_UPLOAD_ERROR, "图像上传失败, 请稍后重试", ipd);
                tryUpload(rm, ipd, client, serverType == ServerType.AUTH);
                if (rm != null && rm.isSuccess()) {
                    return rm;
                }
            } catch (DeployException e) {
                log.error(e.getMessage(), e);
                try {
                    ftpPool.invalidateObject(client);
                    client = null;
                } catch (Exception e1) {
                    log.error(e1.getMessage(), e1);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                if (client != null) {
                    try {
                        ftpPool.returnObject(client);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }

        return new ResultMessage<ImageProcessData>(false, ManipulateConstants.FTP_UPLOAD_ERROR, "图像上传失败, 请稍后重试", ipd);
    }

    public ResultMessage<String> deployFile(String shortType, String localDirectory, String filename) {
        return deployFile(shortType, localDirectory, null, filename);
    }

    public ResultMessage<String> deployFile(String shortType, String localDirectory, String remoteDirectory, String filename) {
        return deployFile(shortType, localDirectory, remoteDirectory, filename, ServerType.NORMAL);
    }

    public ResultMessage<String> deployFile(String shortType, String localDirectory, String remoteDirectory, String filename, ServerType serverType) {
        ResultMessage<FileProcessData> fprm = deployFile2(shortType, localDirectory, remoteDirectory, filename, serverType);

        ResultMessage<String> rmlast = new ResultMessage<>(fprm.isSuccess(), fprm.getCode(), fprm.getMessage(), "");
        if(fprm.getData() !=null){
            rmlast.setData(fprm.getData().getUrl());
        }

        return rmlast;
    }

    public ResultMessage<FileProcessData> deployFile2(String shortType, String localDirectory, String remoteDirectory, String filename, ServerType serverType) {
        final GenericObjectPool<IFtpClient> ftpPool = _getFtpPool(serverType);
        int retry = Math.max(1, getStoreSetting().getRetryTimes());
        while (retry-- > 0) {
            IFtpClient client = null;
            try {
                client = ftpPool.borrowObject();

                ResultMessage<FileProcessData> rm = new ResultMessage<>(false, ManipulateConstants.FTP_UPLOAD_ERROR, "文件上传失败, 请稍后重试", new FileProcessData());
                tryUploadFile(rm, shortType, localDirectory, remoteDirectory, filename,  client, serverType == ServerType.AUTH);

                if (rm != null && rm.isSuccess()) {
                    return rm;
                }
            } catch (DeployException e) {
                log.error(e.getMessage(), e);
                try {
                    ftpPool.invalidateObject(client);
                    client = null;
                } catch (Exception e1) {
                    log.error(e1.getMessage(), e1);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                if (client != null) {
                    try {
                        ftpPool.returnObject(client);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }

        return new ResultMessage<FileProcessData>(false, ManipulateConstants.FTP_UPLOAD_ERROR, "文件上传失败, 请稍后重试", null);
    }

    //删除文件, 所以即使上传服务器已经满了, 也要存在配置里, 以删除文件.
    //后续考虑支持innerUrl的判断: 目前没人用删除文件, 暂不改 2019.8 Felix
    @Override
    public ResultMessage<String> deleteFile(List<String> fileUrls) {
        if(fileUrls == null || fileUrls.size() <=0) {
            return new ResultMessage<>(false, ManipulateConstants.SUCCESS, "删啥?", "");
        }

        Collections.sort(fileUrls); //方便同一个Server的共用FTP连接

        boolean success = true;

        DeployServerItem destServer = null;
        FtpFileDeployClient ftpclient = null;

        for(String url : fileUrls){

            if(destServer == null || !url.startsWith(destServer.getUrl())){

                destServer = null;
                if(ftpclient !=null){
                    ftpclient.disconnect();
                    ftpclient = null;
                }

                //find right server
                for(DeployServerItem server: getStoreSetting().getServers()){
                    if(url.startsWith(server.getUrl())){
                        destServer = server;
                        break;
                    }
                }

                if(destServer == null) {
                    success = false;
                    continue;
                }
                else {
                    ftpclient = createClient(destServer);
                    if(ftpclient == null){
                        success = false;
                        destServer = null;
                        continue;
                    }
                }
            }

            try {
                //上传文件
                String filename = url.substring(destServer.getUrl().length());
                ftpclient.deleteOneFile(filename);
            }
            catch (DeployException e){
                log.error(e.getMessage(), e);
                success = false;
            }
        }

        if(ftpclient !=null){
            ftpclient.disconnect();
        }

        if(success){
            return new ResultMessage<String>(true, ManipulateConstants.SUCCESS, "ok", "");
        }

        return new ResultMessage<String>(false, ManipulateConstants.FTP_DELETE_ERROR, "有错误发生, 也有可能是部分成功+部分失败", "");
    }

    private FtpFileDeployClient createClient(DeployServerItem server){
        try {
            FtpFileDeployClient ftpclient = new FtpFileDeployClient(server, poolId);
            ftpclient.connect();

            return  ftpclient;
        }
        catch (DeployException e){
            log.error(e.getMessage(), e);
            //do nothing
        }

        return null;
    }

    private void tryUpload(ResultMessage<ImageProcessData> rm, ImageProcessData ipd, IFtpClient ftpclient, boolean security) throws DeployException {

        String destDirectoryName = DeployFileUtils.getUploadDirectoryNames(ipd.getShortType());
        log.info("destDirectoryName======>"+destDirectoryName);
        // connect
        // IFtpClient ftpclient = new FtpFileDeployClient(server);
        // 检查并创建目录
        ftpclient.createDirs(destDirectoryName);

        // 上传文件, 设置网址
        ftpclient.deployFile(ipd.getParentFileDirectory(), ipd.getSourceImageName(), destDirectoryName, true);

        String url = linkURL(ftpclient.getServerUrl(), destDirectoryName, ipd.getSourceImageName());
        if(security) {
            url = ImageUtils.switchSign(url);
        }
        ipd.setSourceImageUrl(url);

        for (ImageNameSize ins : ipd.getThumbnailImages()) {
            ftpclient.deployFile(ipd.getParentFileDirectory(), ins.getFilename(), destDirectoryName, true);

            String aurl = ftpclient.getServerUrl() + destDirectoryName + "/" + ins.getFilename();
            if(security) {
                aurl = ImageUtils.switchSign(aurl);
            }
            ins.setUrl(aurl);
        }

        rm.setSuccess(true);
        rm.setCode(0);
        rm.setMessage("图像上传成功，赞");

        log.info("{}#{} image deploy successed: {}",ftpclient.getPoolId(), ftpclient.getConnectId(), ipd.getSourceImageUrl());
    }

    private void tryUploadFile(ResultMessage<FileProcessData> rm, String shortType, String localDirectory, String remoteDirectory,
                               String filename, IFtpClient ftpclient, boolean security) throws DeployException {

        String destDirectoryName;
        if(StringUtils.isBlank(remoteDirectory)){
            destDirectoryName = DeployFileUtils.getUploadDirectoryNames(shortType);
        }
        else{
            destDirectoryName = shortType +  DateFormatUtils.format(new Date(), "yy/") + remoteDirectory;
        }

        // connect
        // IFtpClient ftpclient = new FtpFileDeployClient(server);
        // 检查并创建目录
        ftpclient.createDirs(destDirectoryName);

        // 上传文件, 设置网址
        ftpclient.deployFile( localDirectory, filename, destDirectoryName, true);

        String url = linkURL(ftpclient.getServerUrl(), destDirectoryName, filename);

        if (security) {

            String innerUrl = linkURL(ftpclient.getInnerServerUrl(), destDirectoryName, filename);
            rm.getData().setInnerUrl(innerUrl);

            url = ImageUtils.switchSign(url);
        }

        rm.getData().setUrl(url);

        //rm.setData(url);

        rm.setSuccess(true);

        log.info("{}#{} file deploy successed: {}",ftpclient.getPoolId(), ftpclient.getConnectId(), url);
        
    }

    private String linkURL(String serverURL, String directoryName, String fileName) {
        //暂时只考虑文件名是中文
        try{
            return serverURL + directoryName + "/" + URLEncoder.encode(fileName, "UTF-8");
        }
        catch (Exception e){
            //do nothing
        }

        return serverURL + directoryName + "/" + fileName;
    }

    public FtpStoreSetting getStoreSetting() {
        if (storeSetting == null) {
            rebuildSetting(false);
        }
        if(storeSetting == null)throw new IllegalArgumentException("no ftp server config");
        return storeSetting;
    }

    public void setStoreSetting(FtpStoreSetting storeSetting) {
        this.storeSetting = storeSetting;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }

    final static ObjectMapper mapper = new ObjectMapper();
    static {
        mapper.setSerializationInclusion(Inclusion.NON_NULL);
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false); //忽略未知字段, 以作将来兼容
    }

    private synchronized void rebuildSetting(boolean force) {
        if (this.storeSetting != null && !force)
            return;
        log.info("build ftp server start...");
        String servers = ConfigCenter.getInstance().getDataAsString(serverPath);
        try {
            FtpStoreSetting setting = mapper.readValue(servers, FtpStoreSetting.class);
            this.storeSetting = setting;
        }
        catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        finally {
            if (subscribeDataChange.compareAndSet(false, true)) {
                ConfigCenter.getInstance().subscribeDataChanges(serverPath, this);
            }
            Collection<GenericObjectPool<IFtpClient>> pools = new ArrayList<>(ftpPools.values());
            ftpPools.clear();
            for (GenericObjectPool<IFtpClient> pool : pools) {
                try {
                    pool.close();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            log.info("build ftp server end...");
        }
    }

    @Override
    public void handleDataChange(String dataPath, byte[] data) throws Exception {
        rebuildSetting(true);
    }

    @Override
    public void handleDataDeleted(String dataPath) throws Exception {
    }

    @Override
    public void close() throws IOException {
        if (subscribeDataChange.compareAndSet(true, false)) {
            ConfigCenter.getInstance().unsubscribeDataChanges(serverPath, this);
        }
    }
    
    public String getCallback(String biz) {
        return getStoreSetting().getAuth().get(biz);
    }
    
    public String getPoolId() {
        return poolId;
    }
}

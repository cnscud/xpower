package com.cnscud.xpower.filemanipulate.store.ftp;

import com.cnscud.xpower.filemanipulate.store.DeployException;
import com.cnscud.xpower.filemanipulate.store.DeployServerItem;

import it.sauronsoftware.ftp4j.FTPClient;
import it.sauronsoftware.ftp4j.FTPFile;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Ftp client for file deploy.
 *
 * @author Felix Zhang  Date 2012-10-24 17:26
 * @version 1.0.0
 */
public class FtpFileDeployClient implements IFtpClient{

    final Logger logger = LoggerFactory.getLogger(getClass());

    static int DEPLOY_TRY_MAX = 3;

    protected DeployServerItem dest;
    protected FTPClient ftp;
    final String poolId;
    final static AtomicLong globalConnectId = new AtomicLong(0);

    private final long connectId;
    public FtpFileDeployClient(DeployServerItem dest, String poolId) {
        this.dest = dest;
        this.poolId = poolId;
        this.connectId = globalConnectId.incrementAndGet();
    }

    public void setDestination(DeployServerItem dest) {
        this.dest = dest;
    }
    
    @Override
    public String getServerUrl() {
        return dest.getUrl();
    }

    @Override
    public String getInnerServerUrl() {
        return dest.getInnerurl();
    }

    @Override
    public void connect() throws DeployException {

        if (dest == null) {
            throw new DeployException("Destination is null");
        }

        try {
            ftp = new FTPClient();

            ftp.connect( dest.getHost(), dest.getPort());

            ftp.login(dest.getUsername(), dest.getPassword());

            //@warning: use PASV mode now (our server not support active mode now, need some settings if need support active mode)
            //ftp.getAdvancedFTPSettings().setConnectMode(FTPConnectMode.PASV);

            //ftp.changeDirectory(getRootPath());
            logger.info("{}#{} connect to {} root={}", poolId, connectId, dest.getHost(),dest.getRootPath());
        }
        catch (Exception e) {
            throw new DeployException(getHeader() + "Exception to " + dest.getHost(), e);
        }
    }

   protected boolean createDir(String path) throws DeployException {
       if (StringUtils.isBlank(path)) {
           return true;
       }

       try {
           ftp.changeDirectory(path);

           return true;
       }
       catch (Exception e) {
           //do nothing
       }

       try {
           ftp.createDirectory(path);
           logger.info("{}#{} Create Directory {}", poolId, connectId, path);
        }
       catch (Exception e) {
           logger.warn(getHeader() + "Create Directory  " + path + " Failed.", e);

           return false;
       }

       return true;
   }
   @Override
   public boolean createDirs(String path) throws DeployException {
       if (StringUtils.isBlank(path)) {
           return true;
       }

       String[] pathAry = path.split("/|\\\\");

       String currentPath = transformPath("");

       for (String item : pathAry) {
           if (StringUtils.isEmpty(item)) {
               continue;
           }

           if (currentPath.endsWith("/")) {
               currentPath += item;
           }
           else {
               currentPath += "/" + item;
           }

           boolean ok = createDir(currentPath);

           if (!ok) {
               return false;
           }
       }

       return true;
   }

   /**
    * Deploy File.
    * <p/>
    * implememt verify in this method.
    *
    * @param localFileName local file name
    * @param destDir      dest file directory
    * @param isBinary      is file binary
    * @return ok or failed
    * @throws DeployException
    */
   @Override
    public boolean deployFile(String localDirectory, String localFileName, String destDir,
                              boolean isBinary) throws DeployException {
       logger.debug(String.format("upload file [%s/%s] to [%s]",localDirectory,localFileName,transformPath(destDir)));

        //upload by check , default check by size
        uploadFileByCheckSize(localDirectory, localFileName, transformPath(destDir), isBinary);

        return true;
    }

    protected boolean uploadFileByCheckSize(String localDirectory, String localFileName, String destDir, boolean isBinary) throws DeployException {
        long fileLength = new File(localDirectory, localFileName).length();

        boolean uploadOk = false;
        int tryCount = 0;

        while (!uploadOk && tryCount <= DEPLOY_TRY_MAX) {
            tryCount++;

            uploadFile(localDirectory, localFileName, destDir, isBinary);

            String targetFileName = destDir + "/" + localFileName;
            long uploadedSize = getRemoteFileSize(targetFileName);

            if (uploadedSize == fileLength) {
                uploadOk = true;
            }
            else {
                logger.warn("Upload File Exception, the size is not same");
            }
        }

        return uploadOk;
    }

    protected long getRemoteFileSize(String remoteFileName) throws DeployException {
        try {
            ftp.setType(FTPClient.TYPE_BINARY);
            return ftp.fileSize(remoteFileName);
        }
        catch (Exception e) {
            throw new DeployException(getHeader() + "Exception when fileSize for " + remoteFileName, e);
        }
    }


    protected void uploadFile(String localDirectory, String localFileName, String destDirectory, boolean isBinary) throws DeployException {
       logger.info("param===>"+localDirectory+"--->"+localFileName+"--->"+destDirectory+"--->"+isBinary);
       try {
            if (isBinary) {
                ftp.setType(FTPClient.TYPE_BINARY);
            }
            else {
                ftp.setType(FTPClient.TYPE_TEXTUAL);
            }

            ftp.changeDirectory(destDirectory);

            ftp.upload(new File(localDirectory, localFileName));
        }
        catch (Exception e) {
            throw new DeployException(getHeader() + "Exception when upload file " + localFileName, e);
        }
    }

    @Override
    public boolean directoryExists(String path) throws DeployException {
        try {
            ftp.changeDirectory(transformPath(path));

            return true;
        }
        catch (Exception e) {
            //do nothing
        }

        return false;
    }
    @Override
    public void deleteFilesInDirectory(String directory) throws DeployException
    {
        try
        {
            ftp.changeDirectory(transformPath(directory));
            FTPFile[] files = ftp.list();

            for (FTPFile ftpfile : files)
            {
                if(ftp.getType() == FTPFile.TYPE_FILE)
                {
                    ftp.deleteFile(transformPath(directory) + "/" + ftpfile.getName());
                }
            }
        }
        catch (Exception e)
        {
            throw new DeployException(getHeader() + "Exception to delete files in " + directory, e);
        }
    }
    @Override
    public void deleteDirectoryAndFiles(String directory) throws DeployException
    {
        deleteFilesInDirectory(directory);

        try
        {
            ftp.deleteDirectory(transformPath(directory));
        }
        catch (Exception e)
        {
            throw new DeployException(getHeader() + "Exception to delete directory: " + directory, e);
        }
    }
    @Override
    public void deleteOneFile(String filename) throws DeployException
    {
        try
        {
            ftp.deleteFile(transformPath(filename));
        }
        catch (Exception e)
        {
            throw new DeployException(getHeader() + "Exception to delete file: " + filename, e);
        }
    }
    @Override
    public boolean downloadFile(String remoteFile, String localDirectory, String localFileName) throws DeployException
    {
        try
        {
            final String rfile = transformPath(remoteFile);
            final File lfile = new File(localDirectory, localFileName);
            logger.info(String.format("down file [%s] to [%s]", rfile,lfile));
            ftp.download(rfile,lfile);
        }
        catch (Exception e)
        {
            throw new DeployException(getHeader() + "Exception to download: " + remoteFile, e);
        }

        return true;
    }


    private String getRootPath() {
        String apath = dest.getRootPath();
        if (StringUtils.isNotBlank(apath)) {
            if (!apath.startsWith("/")) {
                apath = "/" + apath;
            }

            if (!apath.endsWith("/")) {
                apath = apath + "/";
            }
        }
        else {
            apath = "/";
        }

        return apath;
    }

    protected String transformPath(String path) {
        String rootPath = getRootPath();

        if (StringUtils.isNotEmpty(path)) {
            return rootPath + path;
        }
        else {
            return rootPath;
        }
    }

    protected String getHeader() {
        String result = "";
        if (dest != null) {
            return "{Ftp: " + dest.getRootPath() + "} ";
        }

        return result;
    }
    @Override
    public void disconnect() {
        if (ftp != null) {
            try {
                if (ftp.isConnected()) {
                    ftp.disconnect(true);
                    logger.info("{}#{} Disconnect from {}", poolId, connectId, dest.getHost());
                }
            }
            catch (Exception e) {
                //do nothing
                logger.warn("Disconnect error", e);
            }
        }
    }

    public String getPoolId() {
        return poolId;
    }
    public long getConnectId() {
        return connectId;
    }
}

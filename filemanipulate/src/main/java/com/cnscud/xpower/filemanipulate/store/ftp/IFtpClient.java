/**
 * 
 */
package com.cnscud.xpower.filemanipulate.store.ftp;

import com.cnscud.xpower.filemanipulate.store.DeployException;

/**
 * 
 * @author adyliu(imxylz@gmail.com)
 * @since 2013年4月3日
 */
public interface IFtpClient {

    String getServerUrl();
    String getInnerServerUrl();

    boolean createDirs(String path) throws DeployException;


    boolean directoryExists(String path) throws DeployException;


    void deleteFilesInDirectory(String directory) throws DeployException;


    void deleteDirectoryAndFiles(String directory) throws DeployException;


    boolean downloadFile(String remoteFile, String localDirectory, String localFileName) throws DeployException;


    void deleteOneFile(String filename) throws DeployException;

    /**
     * 
     */
    void disconnect();


    boolean deployFile(String localDirectory, String localFileName, String destDir, boolean isBinary) throws DeployException;


    void connect() throws DeployException;

    String getPoolId();
    long getConnectId();
}

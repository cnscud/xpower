package com.cnscud.xpower.filemanipulate.store.ftp;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.cnscud.xpower.filemanipulate.store.DeployException;
import com.cnscud.xpower.filemanipulate.store.DeployFileUtils;
import com.cnscud.xpower.filemanipulate.store.DeployServerItem;
import com.cnscud.xpower.filemanipulate.store.DeployServerItem.ServerType;

import static org.junit.Assert.assertEquals;

/**
 * Test case.
 *
 * @author Felix Zhang  Date 2012-10-24 20:14
 * @version 1.0.0
 */
public class FtpFileDeployClientTest {

    String testFileDir = "./testfile";
    String tempFileDir = "./testfile/temp";

    @Before
    public void create() throws Exception{
        FileUtils.deleteDirectory(new File(tempFileDir));
        new File(tempFileDir).mkdirs();
    }
    
    @After
    public void clean() throws Exception{
        FileUtils.deleteDirectory(new File(tempFileDir));
    }
    
    @Test
    public void testUploadFile() throws Exception{
        String file1 = "avatar_fake.jpg";
        String file2 = "avatar_source1.jpg";

        String destDirectoryName = DeployFileUtils.getUploadDirectoryNames("test");
        final FtpDeployFileWorker worker = new FtpDeployFileWorker();
        FtpFileDeployClient ftpclient = new FtpFileDeployClient(worker.getStoreSetting().getServers().get(0), worker.getPoolId());
        try {
            ftpclient.connect();

            //检查并创建目录
            ftpclient.createDirs(destDirectoryName);

            //上传文件
            ftpclient.deployFile(testFileDir, file1, destDirectoryName, true);
            ftpclient.deployFile(testFileDir, file2, destDirectoryName, true);

        }
        catch (DeployException e){
            e.printStackTrace();
        }
        finally {
            ftpclient.disconnect();
            worker.close();
        }

        try {
            ftpclient.connect();

            //上传文件
            ftpclient.downloadFile(destDirectoryName + "/" + file1, tempFileDir, file1);
            ftpclient.downloadFile(destDirectoryName + "/" + file2, tempFileDir, file2);

            ftpclient.deleteDirectoryAndFiles(destDirectoryName);
        }
        finally {
            ftpclient.disconnect();
        }

        assertEquals(new File(testFileDir, file1).length(), new File(tempFileDir, file1).length());
        assertEquals(new File(testFileDir, file2).length(), new File(tempFileDir, file2).length());

        new File(tempFileDir, file1).deleteOnExit();
        new File(tempFileDir, file2).deleteOnExit();

    }

    private DeployServerItem getTestServer(){

        DeployServerItem server = new DeployServerItem();
        server.setId(1);
        server.setName("test");
        server.setHost("192.168.6.23");
        server.setPort(21);
        server.setProtocol("ftp");
        server.setRootPath("/data1");
        server.setUsername("upload");
        server.setPassword("goearth");
        server.setUrl("http://test1.sjb.bz/");
        server.setStatus(DeployServerItem.STATUS_NORMAL);
        server.setType(ServerType.NORMAL);

        return server;

    }

}

package com.cnscud.xpower.filemanipulate.store;

import com.cnscud.xpower.filemanipulate.ManipulateConstants;
import com.cnscud.xpower.filemanipulate.image.FileProcessData;
import com.cnscud.xpower.filemanipulate.image.ImageManipulater;
import com.cnscud.xpower.filemanipulate.image.ImageNameSize;
import com.cnscud.xpower.filemanipulate.image.ImageProcessData;
import com.cnscud.xpower.filemanipulate.image.ResultMessage;
import com.cnscud.xpower.filemanipulate.store.DeployServerItem.ServerType;
import com.cnscud.xpower.filemanipulate.store.ftp.FtpDeployFileWorker;
import com.cnscud.xpower.filemanipulate.store.ftp.FtpStoreSetting;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test case.
 *
 * @author Felix Zhang  Date 2012-10-24 10:27
 * @version 1.0.0
 */
public class DeployFileWorkerTest extends TestCase {

    private static Log log = LogFactory.getLog(DeployFileWorkerTest.class);


    String testfileDirectory = "./testfile";


    String userPrefix = "12345"; //for test

    public void testDeployFile() throws IOException {


        String filename1 = "avatar_source1.jpg";

        String tempfilename1 = "temp1_avatar.jpg";
        FileUtils.copyFile(new File(testfileDirectory, filename1), new File(testfileDirectory, tempfilename1));


        ImageNameSize[] requiredSizes =
                {ImageNameSize.n("b", 120, 120), ImageNameSize.n("m", 60, 60), ImageNameSize.n("s", 24, 24)};


        ImageManipulater im = new ImageManipulater();

        ResultMessage<ImageProcessData> rm1 = im.processAvatarRawFile(new File(testfileDirectory, tempfilename1),
                requiredSizes, userPrefix);


        //文件名 = userid_imageid_sizesymbol.suffix + 服务器标识??
        ImageProcessData ifs = rm1.getData();
        assertNotNull(ifs.getSourceImageName());
        assertEquals(3, ifs.getThumbnailImages().length);

        FtpStoreSetting setting = new FtpStoreSetting();
        setting.setServers(getTestServers());


        FtpDeployFileWorker storeWorker = new FtpDeployFileWorker();
        storeWorker.setStoreSetting(setting);

        ResultMessage<ImageProcessData> uploadRM = storeWorker.deployImages(rm1.getData());

        ImageProcessData uploadData = uploadRM.getData();
        assertNotNull(uploadData.getSourceImageUrl());
        assertEquals(3, uploadData.getThumbnailImages().length);

        for(ImageNameSize ins: uploadData.getThumbnailImages()){
            log.debug(ins.getUrl());
            assertTrue(ins.getUrl().startsWith("http://test1.sjbly.cn")
                    || ins.getUrl().startsWith("http://test2.sjbly.cn"));
        }

        FileUtils.deleteDirectory(new File(ifs.getParentFileDirectory()));
        storeWorker.close();
    }

    public void testUploadFile() throws Exception{
        String fname = "中文123abc.jpg";

        File tmpFile = new File(testfileDirectory, fname);

        FtpStoreSetting setting = new FtpStoreSetting();
        setting.setServers(getTestServers());


        FtpDeployFileWorker storeWorker = new FtpDeployFileWorker();
        storeWorker.setStoreSetting(setting);

        ResultMessage<String> uploadRM = storeWorker.deployFile(ManipulateConstants.ShortName_GeneralFile, testfileDirectory, "tmp", fname);

        if (uploadRM.isSuccess()) {
            log.debug(uploadRM.getData());
        }
        storeWorker.close();
    }

    public void testDeleteFiles() throws Exception{
        List<String> urls = new ArrayList<>();
        urls.add("http://test1.sjbly.cn/test1/browserconfig.xml");
        urls.add( "http://test2.sjbly.cn/test1/aaa.txt" );
        urls.add("http://test1.sjbly.cn/test1/IMG_5474.JPG");
        urls.add("http://test1.sjbly.cn/test1/1.bin");

        FtpStoreSetting setting = new FtpStoreSetting();
        setting.setServers(getTestServers());

        FtpDeployFileWorker worker = new FtpDeployFileWorker();
        worker.setStoreSetting(setting);

        ResultMessage msg = worker.deleteFile(urls);
        log.debug(msg.getCode());
        worker.close();

    }


    public void testUploadInnerFile() throws Exception{
        String fname = "readme.txt";

        File tmpFile = new File(testfileDirectory, fname);

        FtpStoreSetting setting = new FtpStoreSetting();
        setting.setServers(getTestServers());


        FtpDeployFileWorker storeWorker = new FtpDeployFileWorker();
        storeWorker.setStoreSetting(setting);

        ResultMessage<FileProcessData> uploadRM = storeWorker.deployFile2(ManipulateConstants.ShortName_InnerFile, testfileDirectory,
                null, fname, ServerType.AUTH);

        if (uploadRM.isSuccess()) {
            log.debug(uploadRM.getData());
        }
        storeWorker.close();
    }


    private List<DeployServerItem> getTestServers(){
        List<DeployServerItem> servers = new ArrayList<DeployServerItem>();

        DeployServerItem server = new DeployServerItem();
        server.setId(1);
        server.setName("test");
        server.setHost("192.168.6.23");
        server.setPort(21);
        server.setProtocol("ftp");
        server.setRootPath("/data1");
        server.setUsername("upload");
        server.setPassword("goearth");
        server.setUrl("http://test1.sjbly.cn/");
        server.setStatus(DeployServerItem.STATUS_NORMAL);
        server.setType(ServerType.NORMAL);

        servers.add(server);

        DeployServerItem server2 = new DeployServerItem();
        server2.setId(1);
        server2.setName("test2");
        server2.setHost("192.168.6.23");
        server2.setPort(21);
        server2.setProtocol("ftp");
        server2.setRootPath("/fs1");

        server2.setUsername("upload");
        server2.setPassword("goearth");
        server2.setUrl("http://fs.panda.com/auth/fs1/");
        server2.setInnerurl("http://fs.panda.com/fs1/");
        server2.setStatus(DeployServerItem.STATUS_NORMAL);
        server2.setType(ServerType.AUTH);

        servers.add(server2);

        return servers;
    }

}

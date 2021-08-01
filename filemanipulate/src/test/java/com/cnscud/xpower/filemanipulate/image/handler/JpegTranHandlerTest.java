package com.cnscud.xpower.filemanipulate.image.handler;

import com.cnscud.xpower.filemanipulate.image.BaseTest;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * test JpegTranHandler.
 *
 * @author Felix Zhang 2016-12-22 18:29
 * @version 1.0.0
 */
public class JpegTranHandlerTest extends BaseTest {


    public void testHandle() throws Exception {

        String file = "asrc1.jpg";

        String tempfilename1 = "temp_" + file;
        File tmpFile = new File(testtempfileDir, tempfilename1);
        FileUtils.deleteQuietly(tmpFile);

        FileUtils.copyFile(new File(testfileDirectory, file), tmpFile);

        System.out.println(tempfilename1);

        JpegTranHandler handler = new JpegTranHandler();
        handler.setDebug(true);

        handler.execute(testtempfileDir, tempfilename1);
    }

}
package com.cnscud.xpower.filemanipulate.image.handler;

import com.cnscud.xpower.filemanipulate.image.BaseTest;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * test PngquantHandler.
 *
 * @author Felix Zhang 2016-12-22 18:29
 * @version 1.0.0
 */
public class PngquantHandlerTest extends BaseTest {


    public void testHandle() throws Exception {

        String file = "src1.png";

        String tempfilename1 = "temp_" + file;
        File tmpFile = new File(testtempfileDir, tempfilename1);
        FileUtils.deleteQuietly(tmpFile);

        FileUtils.copyFile(new File(testfileDirectory, file), tmpFile);

        System.out.println(tempfilename1);

        PngquantHandler handler = new PngquantHandler();
        handler.setDebug(true);

        handler.execute(testtempfileDir, tempfilename1);
    }

}
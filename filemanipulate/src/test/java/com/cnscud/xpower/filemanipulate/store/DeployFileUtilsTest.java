package com.cnscud.xpower.filemanipulate.store;

import junit.framework.TestCase;

import java.util.Date;

/**
 * Testcase.
 *
 * @author Felix Zhang  Date 2012-10-24 18:39
 * @version 1.0.0
 */
public class DeployFileUtilsTest extends TestCase {

    public void testGetUploadDirectoryNames(){
        Date now = new Date();
        String result = DeployFileUtils.getUploadDirectoryNames("test");

        //System.out.println(result);

        assertNotNull(result);


    }
}

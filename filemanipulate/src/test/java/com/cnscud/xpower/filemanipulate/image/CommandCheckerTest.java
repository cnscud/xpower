package com.cnscud.xpower.filemanipulate.image;

import junit.framework.TestCase;

/**
 *  .
 *
 * @author Felix Zhang 2016-12-28 17:24
 * @version 1.0.0
 */
public class CommandCheckerTest extends TestCase {
    public void testReadCommandAvailableStatus() throws Exception {
        System.out.println(CommandChecker.readCommandAvailableStatus("convert"));
        System.out.println(CommandChecker.readCommandAvailableStatus("pngquant"));
        System.out.println(CommandChecker.readCommandAvailableStatus("testaaa"));
    }

    public void testIsCommandAvailable() throws Exception {
        System.out.println(CommandChecker.isCommandAvailable("convert"));
        System.out.println(CommandChecker.isCommandAvailable("pngquant"));
    }

    public void testCheckCommandReady() throws Exception {
        System.out.println(CommandChecker.checkCommandReady("convert"));
        System.out.println(CommandChecker.checkCommandReady("pngquant"));
    }

}
/**
 * 
 */
package com.cnscud.xpower.filemanipulate.image.impl;

import java.io.File;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import junit.framework.Assert;
import net.coobird.thumbnailator.util.exif.ExifUtils;
import net.coobird.thumbnailator.util.exif.Orientation;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2013年10月11日
 */
public class ImageInfoCheckerImplTest {

    /**
     * Test method for {@link ImageInfoCheckerImpl#check(java.io.File)}.
     */
    @Test
    public void testCheck() throws Exception {
        File srcFile = new File("testfile/Landscape_2.jpg");
        File destFile = new File("testfile/Landscape_2_new");
        FileUtils.copyFile(srcFile, destFile);
        ImageInfoCheckerImpl checker = new ImageInfoCheckerImpl();
        boolean ret = checker.check(destFile);
        Assert.assertTrue(ret);
    }

    @Test
    public void exifOrientation2() throws Exception {
        File srcFile = new File("testfile/Landscape_2.jpg");
        File destFile = new File("testfile/Landscape_2_new.jpg");
        FileUtils.copyFile(srcFile, destFile);
        // given
        ImageReader reader = ImageIO.getImageReadersByFormatName("jpg").next();
        reader.setInput(ImageIO.createImageInputStream(destFile));

        // when
        Orientation orientation = ExifUtils.getExifOrientation(reader, 0);

        // then
        Assert.assertEquals(Orientation.typeOf(2), orientation);
    }

}

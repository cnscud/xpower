package com.cnscud.xpower.filemanipulate.image.engager;

import com.cnscud.xpower.filemanipulate.ManipulateConstants;
import com.cnscud.xpower.filemanipulate.image.ImageNameSize;

/**
 * Test.
 *
 * @author Felix Zhang 2016-12-27 16:14
 * @version 1.0.0
 */
public class ThumbnailatorEngagerTest extends BaseEngagerTest {

    ThumbnailatorEngager engager = new ThumbnailatorEngager();

    protected ImageEngager getEngager(){
        return engager;
    }

    public void testResizeImage() throws Exception {

        String[] files = {"src1.png", "src2_icon.png", "image_png.png"};

        ImageNameSize[] requiredSizes = ImageNameSize.convert(ManipulateConstants.FULL_SIZE);
        //{ImageNameSize.n("b", 800, 600), ImageNameSize.n("m", 150, 150), ImageNameSize.n("s", 24, 24)};

        dealFiles4Engager("tn_", files, requiredSizes);
    }

    public void testTransferImage() throws Exception {

        String[] files = {"src1.png", "src2_icon.png", "image_png.png"};

        dealFile4Transfer("tn_", files);
    }

}
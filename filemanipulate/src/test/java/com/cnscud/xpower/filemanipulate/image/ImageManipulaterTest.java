package com.cnscud.xpower.filemanipulate.image;

import com.cnscud.xpower.filemanipulate.ManipulateConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Test case.
 *
 * @author Felix Zhang  Date 2012-10-23 14:45
 * @version 1.0.0
 */
public class ImageManipulaterTest extends BaseTest {



    public void testProcessImageAvatar() throws IOException {

        String filename1 = "avatar_source1.jpg";
        String filename2 = "image_gif.gif";

        String tempfilename1 = "temp" + filename1;
        String tempfilename2 = "temp" + filename2;
        FileUtils.copyFile(new File(testfileDirectory, filename1), new File(testfileDirectory, tempfilename1));
        FileUtils.copyFile(new File(testfileDirectory, filename2), new File(testfileDirectory, tempfilename2));


        ImageNameSize[] requiredSizes =
                {ImageNameSize.n("b", 150, 150), ImageNameSize.n("m", 100, 100), ImageNameSize.n("s", 16, 16)};


        ImageManipulater im = new ImageManipulater();

        ResultMessage<ImageProcessData> rm1 = im.processAvatarRawFile(new File(testfileDirectory, tempfilename1),
                requiredSizes, userPrefix);

        ResultMessage<ImageProcessData> rm2 = im.processAvatarRawFile(testfileDirectory, tempfilename2,
                requiredSizes, userPrefix);


        //测试几个尺寸的头像文件是否生成, 并检测头像宽高是否正确

        //文件名 = userid_imageid_sizesymbol.suffix + 服务器标识??
        ImageProcessData ifs = rm1.getData();
        assertNotNull(ifs.getSourceImageName());
        assertEquals(3, ifs.getThumbnailImages().length);

        String imageId = ifs.getImageId();
        assertNotNull(imageId);
        File originalFile = new File(ifs.getParentFileDirectory(), ifs.getSourceImageName());
        assertTrue(originalFile.exists());

        File bigImage = new File(ifs.getParentFileDirectory(), ifs.getThumbnailImages()[0].getFilename());
        assertTrue(bigImage.exists());


        im.cleanTemporaryFiles(new File(testfileDirectory, tempfilename1), rm1.getData());
        im.cleanTemporaryFiles(new File(testfileDirectory, tempfilename2), rm2.getData());
    }

    public void testProcessFakeImageAvatar() throws IOException {

        String filename1 = "avatar_fake.jpg";
        String tempfilename1 = "temp1_fake.jpg";

        FileUtils.copyFile(new File(testfileDirectory, filename1), new File(testfileDirectory, tempfilename1));



        ImageNameSize[] requiredSizes =
                {ImageNameSize.n("b", 150, 150), ImageNameSize.n("m", 100, 100), ImageNameSize.n("s", 16, 16)};


        ImageManipulater im = new ImageManipulater();

        ResultMessage<ImageProcessData> rm1 = im.processAvatarRawFile(new File(testfileDirectory, tempfilename1),
                requiredSizes, userPrefix);

        assertFalse(rm1.isSuccess());

        if(rm1.getData() !=null) {
            im.cleanTemporaryFiles(new File(testfileDirectory, tempfilename1), rm1.getData());
        }

    }

    public void testCheckAndProcessFakeAvatar() throws IOException {

        String filename1 = "avatar_fake.jpg";
        String tempfilename1 = "temp1_fake.jpg";

        FileUtils.copyFile(new File(testfileDirectory, filename1), new File(testfileDirectory, tempfilename1));



        ImageNameSize[] requiredSizes =
                {ImageNameSize.n("b", 150, 150), ImageNameSize.n("m", 100, 100), ImageNameSize.n("s", 16, 16)};


        ImageManipulater im = new ImageManipulater();

        ResultMessage<ImageProcessData> rm1 = im.processAvatarRawFile(new File(testfileDirectory, tempfilename1),
                requiredSizes, userPrefix);

        assertFalse(rm1.isSuccess());
    }

    //普通图像
    public void testProcessGeneralImage() throws Exception {
        String filename1 = "image_source1.jpg";
        String filename2 = "image_source2.jpg";
        String filenameGIF = "image_gif.gif";
        String filenamePNG = "image_png.png";
        String filenameBMP = "image_bmp.bmp";
        String filenameNoSuffix = "image_nof";

        String[] files = {filename1, filename2, filenameGIF, filenameBMP, filenamePNG, filenameNoSuffix};


        ImageNameSize[] requiredSizes =
                {ImageNameSize.n("b", 800, 600), ImageNameSize.n("m", 150, 150), ImageNameSize.n("s", 24, 24)};

        dealFiles(files, requiredSizes);
    }


    //特殊格式
    public void testProcessGeneralImageByFormat() throws Exception {

        String[] files = { "format/rubyfangtto_o.jpg"};

        //"format/3827jqt_o.jpg",  Numbers of source Raster bands and source color space components do not match

        ImageNameSize[] requiredSizes =
                {ImageNameSize.n("b", 800, 600), ImageNameSize.n("m", 150, 150), ImageNameSize.n("s", 24, 24)};

         dealFiles(files, requiredSizes);
    }

    //特殊格式
    public void testProcessGifImage() throws Exception {

        String[] files = { "image_gif.gif"};

        //"format/3827jqt_o.jpg",  Numbers of source Raster bands and source color space components do not match

        ImageNameSize[] requiredSizes =
                {ImageNameSize.n("b", 800, 0), ImageNameSize.n("fm", 150, 150), ImageNameSize.n("s", 24, 24)};

        dealFiles(files, requiredSizes);
    }

    //普通
    public void testProcessGeneralImageUnderFullFilled() throws Exception {
        String filename1 = "image_source1.jpg";
        String filename2 = "image_source2.jpg";
        String filenameGIF = "image_gif.gif";
        String filenamePNG = "image_png.png";
        String filenameBMP = "image_bmp.bmp";
        String filenameNoSuffix = "image_nof";

        String[] files = {filename1, filename2, filenameGIF, filenameBMP, filenamePNG, filenameNoSuffix};


        ImageNameSize[] requiredSizes =
                {ImageNameSize.n("fb", 900, 600),ImageNameSize.n("b", 570, 0), ImageNameSize.n("fc", 315, 210), ImageNameSize.n("fs", 150, 100)};

        dealFiles(files, requiredSizes);

    }

    public void testSamplePNGFile() throws Exception {
        String[] files = {"src1.png",   "src2_bigicon.png", "image_png.png" , "src_indexed.png" };

        ImageNameSize[] requiredSizes = ImageNameSize.convert(ManipulateConstants.FULL_SIZE);
        //{ImageNameSize.n("b", 800, 600), ImageNameSize.n("m", 150, 150), ImageNameSize.n("s", 24, 24)};

        dealFiles(files, requiredSizes, false);
    }


    private void dealFiles(String[] files, ImageNameSize[] requiredSizes) throws IOException {
        dealFiles(files, requiredSizes, true);
    }

    private void dealFiles(String[] files, ImageNameSize[] requiredSizes, boolean keepImageFormat) throws IOException {
        ImageManipulater im = new ImageManipulater();
        im.debug();
        //im.useHandler(false);

        for(String file: files){

            String tempfilename1 = "temp_" + file;
            FileUtils.copyFile(new File(testfileDirectory, file), new File(testfileDirectory, tempfilename1));

            System.out.println(tempfilename1);

            ResultMessage<ImageProcessData> rm1 = im._processGeneralImageFile(new File(testfileDirectory, tempfilename1),
                    requiredSizes, userPrefix, "test", true, keepImageFormat, null, "wm1.png|BOTTOM_RIGHT");

            ImageProcessData ifd = rm1.getData();
            assertNotNull(ifd.getSourceImageName());
            assertEquals(requiredSizes.length, ifd.getThumbnailImages().length);

            if(ifd.getSourceImageName() !=null){
                File afile = new File(ifd.getParentFileDirectory(), ifd.getSourceImageName());
                assertTrue(afile.exists());
            }

            if(ifd.getRawSourceImageName() !=null){
                File afile = new File(ifd.getParentFileDirectory(), ifd.getRawSourceImageName());
                assertTrue(afile.exists());
            }

            //清理现场
            //im.cleanTemporaryFiles(new File(testfileDirectory, tempfilename1), ifd);
        }
    }

    //普通
    public void testWatermark() throws Exception {
        String filename2 = "Landscape_2.jpg";

        String[] files = {filename2  };

        ImageNameSize[] requiredSizes = ImageNameSize.convert(ManipulateConstants.FULL_SIZE);
                //{ImageNameSize.n("fb", 900, 600),ImageNameSize.n("b", 570, 0), ImageNameSize.n("fc", 315, 210), ImageNameSize.n("fs", 150, 100)};

        dealFiles(files, requiredSizes);
    }

    //普通
    public void testVeryHighImage() throws Exception {
        String[] files = {"veryhigh.jpg"};

        ImageNameSize[] requiredSizes = ImageNameSize.convert(ManipulateConstants.FULL_SIZE);

        dealFiles(files, requiredSizes);
    }

    //普通
    public void testHighQualityJpgButSmallSize() throws Exception {
        String[] files = {"jpg_smallwh_highquality.jpg", "image_source1.jpg"};

        ImageNameSize[] requiredSizes = ImageNameSize.convert(ManipulateConstants.FULL_SIZE);

        dealFiles(files, requiredSizes);
    }

    public void testOptimizedFile() throws Exception {
        String[] files = {"adest1.jpg", "pq_dest1.png"};

        ImageNameSize[] requiredSizes =
                {ImageNameSize.n("b", 800, 600), ImageNameSize.n("m", 150, 150), ImageNameSize.n("s", 24, 24)};

        dealFiles(files, requiredSizes);
    }

    public void testProcessGeneralImageWithoutHeight() throws Exception {
        String filename1 = "image_source1.jpg";
        String filename2 = "image_source2.jpg";
        String filenameGIF = "image_gif.gif";
        String filenamePNG = "image_png.png";
        String filenameBMP = "image_bmp.bmp";
        String filenameNoSuffix = "image_nof";

        String[] files = {filename1, filename2, filenameGIF, filenameBMP, filenamePNG, filenameNoSuffix};


        ImageNameSize[] requiredSizes =
                {ImageNameSize.n("b", 800, 0), ImageNameSize.n("m", 150, 120), ImageNameSize.n("s", 0, 24)};

        dealFiles(files, requiredSizes);
    }
    
    private static String getSizePairByCalc(int width, int height, ImageNameSize expectSize){
        IImageSize realSize = ImageUtils.calcRealDimensionByExpectSize(width, height, expectSize);
        return realSize.getWidth() + "x" + realSize.getHeight();
    }


    //普通头像
    public void testResizeDimension() throws Exception {
        assertEquals("2000x1600", ImageManipulater.calcDimension(3000, 2400));
        assertEquals("1600x2000", ImageManipulater.calcDimension(2400, 3000));
        assertEquals("2000x1000", ImageManipulater.calcDimension(3000, 1500));
        assertEquals("1000x2000", ImageManipulater.calcDimension(1500, 3000));
        assertEquals("1500x1500", ImageManipulater.calcDimension(1500, 1500));
    }

    public void testCalcSignedDimensionForFullBig() throws Exception {
        ImageNameSize ins = new ImageNameSize("fb", 570, 380);
        assertEquals("570x380", getSizePairByCalc(670, 480, ins));
        assertEquals("570x380", getSizePairByCalc(570, 380, ins));
        assertEquals("470x313", getSizePairByCalc(470, 380, ins));
        assertEquals("540x360", getSizePairByCalc(570, 360, ins));
        assertEquals("470x313", getSizePairByCalc(470, 360, ins));
        assertEquals("450x300", getSizePairByCalc(540, 300, ins));
    }

    public void testCalcSignedDimensionForFullCover() throws Exception {
        ImageNameSize ins = new ImageNameSize("fb", 630, 420);
        assertEquals("200x133", getSizePairByCalc(200, 200, ins));
        assertEquals("500x333", getSizePairByCalc(500, 500, ins));
        assertEquals("450x300", getSizePairByCalc(800, 300, ins));
    }

    public void testCalcSignedDimensionForGeneralBig() throws Exception {
        ImageNameSize ins = new ImageNameSize("b", 570, 0);
        assertEquals("570x578", getSizePairByCalc(670, 680, ins));
        assertEquals("570x380", getSizePairByCalc(570, 380, ins));
        assertEquals("470x380", getSizePairByCalc(470, 380, ins));
        assertEquals("540x360", getSizePairByCalc(540, 360, ins));
        assertEquals("470x360", getSizePairByCalc(470, 360, ins));
        assertEquals("540x300", getSizePairByCalc(540, 300, ins));
    }

    public void testGetSignSize(){
        ImageNameSize ins = new ImageNameSize("b", 900, 0);
        assertEquals("570x578", getSizePairByCalc(440, 9406, ins));


    }

    public void testPlugin(){
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("JPEG");
        while (readers.hasNext()) {
            System.out.println("reader: " + readers.next());
        }
    }

    IImageSize PICTURE_BIGSIZE = ManipulateConstants.PICTURE_BIGSIZE;
    IImageSize PICTURE_HEADSIZE = ManipulateConstants.PICTURE_HEADSIZE;
    IImageSize PICTURE_MEDIUMSIZE = ManipulateConstants.PICTURE_MEDIUMSIZE;
    IImageSize PICTURE_COVERSIZE = ManipulateConstants.PICTURE_COVERSIZE;
    IImageSize PICTURE_SMALLSIZE = ManipulateConstants.PICTURE_SMALLSIZE;

    IImageSize[] PICTURE_SIZES = new IImageSize[] { PICTURE_BIGSIZE, PICTURE_MEDIUMSIZE, PICTURE_HEADSIZE, PICTURE_SMALLSIZE, PICTURE_COVERSIZE };

    public String pickSignSize(int width, int height){

        IImageSize signedSize = new ImageNameSize("base", ManipulateConstants.IMAGE_BASE_WIDTH, ManipulateConstants.IMAGE_BASE_HEIGHT);

        IImageSize bigSize = null;
        IImageSize fbigSize = null;

        for (IImageSize ins : PICTURE_SIZES) {
            // 避免同时出现!
            if (ins.getSuffix().endsWith("b")&&!ins.getSuffix().startsWith("f")) { // 约定
                bigSize = ins;
            }

            if (ins.getSuffix().startsWith("f") && ins.getSuffix().endsWith("b") && bigSize == null) { // 约定
                fbigSize = ins;
            }
        }
        signedSize = bigSize != null ? bigSize : fbigSize != null ? fbigSize : signedSize;

        String sizeInfo = "";
        IImageSize realSignedSize = ImageUtils.calcRealDimensionByExpectSize(width, height, signedSize);
        if (signedSize != null) {
            sizeInfo = realSignedSize.getWidth() + "x" + realSignedSize.getHeight();
        }

        return sizeInfo;
    }

    public static List<Integer> toIntegerList(List<String> numbers, boolean ignoreError) {
        if (numbers == null || numbers.isEmpty())
            return new ArrayList<Integer>(0);
        List<Integer> ret = new ArrayList<Integer>(numbers.size());
        for (String number : numbers) {
            try {
                number = number.trim();
                Integer v = Integer.valueOf(number);
                ret.add(v);
            } catch (NumberFormatException ex) {
                if (!ignoreError) {
                    throw ex;
                }
            }
        }
        return ret;
    }

    public static List<String> splitWithTrim(String s, String sep) {
        if (StringUtils.isBlank(s)) {
            return new ArrayList<String>(0);
        }
        String[] arr = StringUtils.split(s, sep);
        List<String> ret = new ArrayList<String>(arr.length);
        for (String a : arr) {
            if (StringUtils.isNotBlank(a)) {
                ret.add(a.trim());
            }
        }
        return ret;
    }

    public void testSizeFromSigned(){
        doTestSignSize(1000, 2000);
        doTestSignSize(1000, 1000);
        doTestSignSize(1200, 800);
        doTestSignSize(800, 1000);
        doTestSignSize(800, 400);
        doTestSignSize(500, 1000);
        doTestSignSize(500, 300);
        doTestSignSize(300, 300);
        doTestSignSize(300, 100);
        doTestSignSize(100, 300);
        doTestSignSize(100, 100);
    }

    private void doTestSignSize(int width, int height) {
        String sign = pickSignSize(width, height);

        System.out.println(" width: " + width + " height: " + height + " result: " + sign);
        List<Integer> list = toIntegerList(splitWithTrim(sign, "x"), true);

        System.out.println("medium real size 1: " + ImageUtils.pickMediumImageSizeByBigSize(list.get(0), list.get(1)));
        System.out.println("medium real size: " + ImageUtils.calcRealDimensionByExpectSize(list.get(0), list.get(1), PICTURE_MEDIUMSIZE));
        System.out.println("fc real size: " + ImageUtils.calcRealDimensionByExpectSize(list.get(0), list.get(1), PICTURE_COVERSIZE));
        System.out.println("---------------");
    }

    public void testFindReplaceName(){
        String filename = "http://f3.sjbly.cn/m14/1226/1136/00wta_300x200_fem.jpg";
        String exceptname = "http://f3.sjbly.cn/m14/1226/1136/00wta_300x200_fes.jpg";

        if(ManipulateConstants.inCategory(filename, "e")){
            assertEquals(exceptname, ManipulateConstants.replace(filename, "fes"));
        }

        filename = "http://f3.sjbly.cn/m14/1226/1136/00wta_o.jpg";
        exceptname = "http://f3.sjbly.cn/m14/1226/1136/00wta_o.jpg";

        if(ManipulateConstants.inCategory(filename, "e")){
            assertEquals(exceptname, ManipulateConstants.replace(filename, "fes"));
        }
    }

}

package com.cnscud.xpower.filemanipulate.image.engager;

import com.cnscud.xpower.filemanipulate.ManipulateConstants;
import com.cnscud.xpower.filemanipulate.image.BaseTest;
import com.cnscud.xpower.filemanipulate.image.IImageInfoChecker;
import com.cnscud.xpower.filemanipulate.image.ImageInfoUtils;
import com.cnscud.xpower.filemanipulate.image.ImageNameSize;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

/**
 * Base.
 *
 * @author Felix Zhang 2016-12-28 10:25
 * @version 1.0.0
 */
public abstract class BaseEngagerTest extends BaseTest {


    protected abstract ImageEngager getEngager();


    protected void dealFiles4Engager(String prefix, String[] files, ImageNameSize[] requiredSizes) throws IOException {


        for(String file: files){

            String tempfilename1 = "temp_" + file;
            FileUtils.copyFile(new File(testfileDirectory, file), new File(testfileDirectory, tempfilename1));

            System.out.println(tempfilename1);

            String imageId = pickNewImageId();
            String imageDirectory = pickImageDirectoryName(prefix + "test", imageId, "1101");// 本地临时目录

            File swapFileDirectory = new File(testfileDirectory, imageDirectory);

            if (!swapFileDirectory.exists()) {
                swapFileDirectory.mkdirs();
            }

            IImageInfoChecker checker = ImageInfoUtils.checkImageInfo(new File(testfileDirectory, file));

            for (ImageNameSize ins : requiredSizes) {
                String sizeInfo = ins.getWidth() + "x" + ins.getHeight();

                String destFileName = pickThumbnailImageFileName(imageId, FilenameUtils.getExtension(file), "1101", ins.getName(), sizeInfo);

                if(ins.getSuffix().startsWith("f")){
                    getEngager().resizeFullFilledImage(checker.getWidth(), checker.getHeight(),new File(testfileDirectory, tempfilename1), ins, new File(swapFileDirectory, destFileName),
                            ManipulateConstants.WaterMark1, ManipulateConstants.WaterMark_Postion_Default);
                }
                else {
                    getEngager().resizeImageNoEnlarge(checker.getWidth(), checker.getHeight(), new File(testfileDirectory, tempfilename1),
                            ins, new File(swapFileDirectory, destFileName),
                            ManipulateConstants.WaterMark1, ManipulateConstants.WaterMark_Postion_Default);

                }
            }

        }
    }

    protected void dealFile4Transfer(String prefix, String[] files) throws IOException{
        for(String file: files) {

            String tempfilename1 = "temp_" + file;

            FileUtils.copyFile(new File(testfileDirectory, file), new File(testfileDirectory, tempfilename1));

            String imageId = pickNewImageId();
            String imageDirectory = pickImageDirectoryName(prefix + "test", imageId, "1101");// 本地临时目录

            File swapFileDirectory = new File(testfileDirectory, imageDirectory);

            if (!swapFileDirectory.exists()) {
                swapFileDirectory.mkdirs();
            }

            getEngager().transferImage(new File(testfileDirectory, tempfilename1), new File(swapFileDirectory, imageId + ".jpg"));
        }

    }


}

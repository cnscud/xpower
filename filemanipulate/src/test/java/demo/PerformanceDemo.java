package demo;

import java.io.File;

import org.apache.commons.lang.time.StopWatch;

import com.cnscud.xpower.filemanipulate.ManipulateConstants;
import com.cnscud.xpower.filemanipulate.image.ImageManipulater;
import com.cnscud.xpower.filemanipulate.image.ImageParam;
import com.cnscud.xpower.filemanipulate.image.ImageProcessData;
import com.cnscud.xpower.filemanipulate.image.ResultMessage;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2016年12月27日
 */
public class PerformanceDemo {


    public static void main(String[] args) {
        final File srcFile = new File("testfile/5184x3456.jpg");
        //
        ImageManipulater im = new ImageManipulater();
        //
        StopWatch sw = new StopWatch();
        sw.start();
        ImageParam p = new ImageParam(srcFile, ManipulateConstants.GENERAL_PICTURE_SIZES, "00", "demo");
        p.withDefaultWatermarkConfig().setKeepImageFormat(true);
        ResultMessage<ImageProcessData> rm = run(im, p);
        System.out.println(rm.isSuccess()+rm.getMessage());
        sw.split();
        System.out.println("cost "+sw.getSplitTime());
        //
        sw.reset();
        sw.start();
        final int n = 0;
        for(int i=0;i<n;i++) {
            run(im,p);
        }
        sw.split();
        System.out.printf("run %d times cost %s\n",n,sw.getSplitTime());
    }

    private static ResultMessage<ImageProcessData> run(ImageManipulater im, ImageParam p) {
        return im.processGeneralImageFile(p);
    }

}

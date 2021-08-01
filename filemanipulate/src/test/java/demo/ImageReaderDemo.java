/**
 * 
 */
package demo;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

import com.cnscud.xpower.filemanipulate.image.IImageInfoChecker;
import com.cnscud.xpower.filemanipulate.image.ImageInfoUtils;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2013年6月18日
 */
public class ImageReaderDemo {

    static long getMemory() {
        return (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024;
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        long st = System.currentTimeMillis();
        Collection<File> files = FileUtils.listFiles(new File("./testfile"), new String[] { "jpg" }, false);
        for (File file : files) {
            IImageInfoChecker checker = ImageInfoUtils.checkImageInfo(file);
            System.out.println("MEMORY USED (KB), ONE => " + getMemory() + " checker=>" + checker);
        }
        System.out.println(files.size() + " cost(ms) " + (System.currentTimeMillis() - st));
        System.out.println("MEMORY USED (KB), BEFORE " + getMemory());
        System.gc();
        System.out.println("MEMORY USED (KB), AFTER " + getMemory());
    }
}

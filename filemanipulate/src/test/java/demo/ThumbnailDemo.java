/**
 * 
 */
package demo;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2013年6月17日
 */
public class ThumbnailDemo {

    /**
     * @param args
     */
    public static void main(String[] args) throws IOException {
        String srcFile = "./testfile/png_1500x1506.png";
        File dest = new File("/tmp/" + UUID.randomUUID().toString() + ".jpg");
        Thumbnails.of(srcFile).scale(1.0).useExifOrientation(true).outputFormat("JPEG").toFile(dest); // 输出到文件
        System.out.println(dest);
    }
}

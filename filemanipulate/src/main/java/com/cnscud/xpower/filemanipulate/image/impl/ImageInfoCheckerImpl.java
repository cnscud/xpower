/**
 * 
 */
package com.cnscud.xpower.filemanipulate.image.impl;

import com.cnscud.xpower.filemanipulate.image.IImageInfoChecker;
import com.cnscud.xpower.filemanipulate.image.ImageInfoChecker;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.util.exif.ExifUtils;
import net.coobird.thumbnailator.util.exif.Orientation;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;

/**
 * 图像检查.
 * 
 * 需要优化的问题: 依赖了thumbnailator, 进行了两次checker @fixme
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2013年6月18日
 */
public class ImageInfoCheckerImpl implements IImageInfoChecker {

    private ImageInfoChecker checker = new ImageInfoChecker();

    @Override
    public int getWidth() {
        return checker.getWidth();
    }

    @Override
    public int getHeight() {
        return checker.getHeight();
    }

    @Override
    public String getFormatName() {
        return checker.getFormatName();
    }

    @Override
    public boolean check(File file) throws Exception {
        return _check(file);
    }

    private boolean _check(File file) throws Exception {
        byte[] data = FileUtils.readFileToByteArray(file);
        if (data == null)
            return false;
        //
        Orientation orientation = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        checker.setInput(bais);
        boolean ret = checker.check();
        if (ret) {
            bais.reset();
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(checker.getFormatName());

            if (readers != null && readers.hasNext()) {
                ImageReader reader = readers.next();
                ImageInputStream iis = null;
                try {
                    if(checker.getFormat() == ImageInfoChecker.FORMAT_JPEG){ //jpeg
                        bais.reset();
                        iis = ImageIO.createImageInputStream(bais);
                        reader.setInput(iis);
                        orientation = ExifUtils.getExifOrientation(reader, 0);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    reader.dispose();
                    if(iis !=null){
                        iis.close();
                    }
                }
            }

            if (orientation != null && orientation != Orientation.TOP_LEFT) {
                bais.reset();
                FileOutputStream fos = new FileOutputStream(file);
                try {
                    Thumbnails.of(bais).scale(1.0).useExifOrientation(true).toOutputStream(fos);
                } finally {
                    fos.close();
                }

                data = FileUtils.readFileToByteArray(file);
                bais = new ByteArrayInputStream(data);

                checker.setInput(bais);
                ret = checker.check();// reset the width and height
            }
        }

        bais = null;// gc
        data = null;// gc
        return ret;

    }
}

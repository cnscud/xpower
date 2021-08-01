package com.cnscud.xpower.filemanipulate.image.handler;

import com.cnscud.xpower.filemanipulate.image.CommandChecker;
import org.im4java.core.ImageCommand;
import org.im4java.core.Operation;
import org.im4java.core.PQOperation;
import org.im4java.core.PngquantCmd;

/**
 * PNG源文件直接处理.
 *
 * 调用pngquant转换图像为256色, 以节省大小.
 *
 *
 * @author Felix Zhang 2016-12-21 18:00
 * @version 1.0.0
 */
public class PngquantHandler extends ImageHandler {

    @Override
    public boolean accept(String extension) {
        return extension != null && extension.toLowerCase().endsWith("png");
    }

    @Override
    public HandleType getHandleType() {
        return HandleType.ALLIMAGE;
    }

    @Override
    public String getName() {
        return "pngquant";
    }

    @Override
    public String getShortName() {
        return "pq";
    }

    @Override
    public boolean available() {
        return CommandChecker.readCommandAvailableStatus("pngquant");
    }

    @Override
    protected Operation getOperation() {
        PQOperation op = new PQOperation();

        op.output(Operation.IMG_PLACEHOLDER);

        op.addImage(); //占位 ;

        return op;
    }

    @Override
    protected ImageCommand getHandlerCmd() {
        return new PngquantCmd();
    }



}

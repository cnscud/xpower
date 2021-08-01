package com.cnscud.xpower.filemanipulate.image.handler;

import com.cnscud.xpower.filemanipulate.image.CommandChecker;
import org.im4java.core.ImageCommand;
import org.im4java.core.JPTOperation;
import org.im4java.core.JpegtranCmd;
import org.im4java.core.Operation;

/**
 * 使用jpegtran进行优化 (用moz版本的 jpegtran, 节省至少5%的空间).
 *
 * @author Felix Zhang 2016-12-21 18:03
 * @version 1.0.0
 */
public class JpegTranHandler extends ImageHandler {

    @Override
    public boolean accept(String extension) {
        return extension != null && extension.toLowerCase().endsWith("jpg");
    }

    @Override
    public HandleType getHandleType() {
        return HandleType.ALLIMAGE; //每次都要再次处理
    }


    @Override
    public String getName() {
        return "jpegtran";
    }

    @Override
    public String getShortName() {
        return "jt";
    }

    @Override
    public boolean available() {
        return CommandChecker.readCommandAvailableStatus("jpegtran");
    }

    @Override
    protected Operation getOperation() {
        JPTOperation op = new JPTOperation();
        op.copy("none").optimize().progressive();

        op.outfile(Operation.IMG_PLACEHOLDER);

        op.addImage(); //占位

        return op;
    }

    @Override
    protected ImageCommand getHandlerCmd() {
        return new JpegtranCmd();
    }




}

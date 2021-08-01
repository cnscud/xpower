package com.cnscud.xpower.filemanipulate.image.handler;

/**
 * 图像处理时的类型.
 *
 * @author Felix Zhang 2016-12-22 12:30
 * @version 1.0.0
 */
public enum HandleType {

    NONE(0), //啥也不处理
    SOURCEIMAGE(1), //原图, 一般用于第一顺序处理, 后续的处理就继承了优势
    ALLIMAGE(2); //所有图, 一般用于最终图上传前的最后一次处理

    private final int value;

    private HandleType(int value) {
        this.value = value;
    }


}

package com.cnscud.xpower.filemanipulate.image;

/**
 * Result Message.
 *
 * @author Felix Zhang  Date 2012-10-23 17:46
 * @version 1.0.0
 */
public class ResultMessage<T> {

    private boolean success = false; //是否成功
    private int code; //错误代码
    private String message; //错误详细
    private T data; //结果数据

    public ResultMessage() {
    }

    public ResultMessage(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ResultMessage(boolean success, int code, String message) {
        this.code = code;
        this.success = success;
        this.message = message;
    }

    public ResultMessage(boolean success, int code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}

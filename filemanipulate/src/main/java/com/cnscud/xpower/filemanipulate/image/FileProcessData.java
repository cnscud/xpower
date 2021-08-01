package com.cnscud.xpower.filemanipulate.image;

/**
 * 文件处理信息.
 *
 * @author Felix Zhang 2019-08-23 14:48
 * @version 1.0.0
 */
public class FileProcessData {
    private String url;

    private String innerUrl; //安全认证方式下的内部URL, 方便自己做认证

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInnerUrl() {
        return innerUrl;
    }

    public void setInnerUrl(String innerUrl) {
        this.innerUrl = innerUrl;
    }


    @Override
    public String toString() {
        return  "url: " + url + "\n" + "innerUrl: " + innerUrl;
    }
}

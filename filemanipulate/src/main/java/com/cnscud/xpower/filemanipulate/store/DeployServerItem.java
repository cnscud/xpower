package com.cnscud.xpower.filemanipulate.store;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

/**
 * one Ftp server information.
 *
 * @author Felix Zhang Date 2012-10-24 14:36
 * @version 1.0.0
 */
public class DeployServerItem {

    public static enum ServerType {
        NORMAL, //
        AUDIT, //
        AUTH// 授权访问资源
        ;
        @JsonCreator
        public static ServerType parse(int v) {
            for (ServerType r : values()) {
                if (r.ordinal() == v) {
                    return r;
                }
            }
            return ServerType.NORMAL;
        }
        @JsonValue
        public int getValue() {
            return this.ordinal();
        }
    }

    private int id;
    private String protocol = "ftp";

    private String name;
    private String host;
    private int port = 21;
    private String username;
    private String password;

    private String mode; // pasv
    private String comment;

    private String verifyMethod; // 文件校验的方法 size, md5, crc, sfv

    private int status;
    private String url;
    private String innerurl; //内网URL, 用于认证方式下的内部网址
    private String rootPath;

    private ServerType type = ServerType.NORMAL; // 服务器用途

    public static int STATUS_NORMAL = 0;
    public static int STATUS_STOP = 1;

    //public static int SERVER_NORMAL = 0;
    //public static int SERVER_AUDIT = 1;

    public boolean isNormal(ServerType serverType) {
        // server.getStatus() == DeployServerItem.STATUS_NORMAL && server.getType() == DeployServerItem.SERVER_NORMAL
        return this.status == STATUS_NORMAL && this.type == serverType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ServerType getType() {
        return type;
    }

    public void setType(ServerType type) {
        this.type = type;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getVerifyMethod() {
        return verifyMethod;
    }

    public void setVerifyMethod(String verifyMethod) {
        this.verifyMethod = verifyMethod;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getInnerurl() {
        return innerurl;
    }

    public void setInnerurl(String innerurl) {
        this.innerurl = innerurl;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DeployServerItem [id=");
        builder.append(id);
        builder.append(", ");
        if (protocol != null) {
            builder.append("protocol=");
            builder.append(protocol);
            builder.append(", ");
        }
        if (name != null) {
            builder.append("name=");
            builder.append(name);
            builder.append(", ");
        }
        if (host != null) {
            builder.append("host=");
            builder.append(host);
            builder.append(", ");
        }
        builder.append("port=");
        builder.append(port);
        builder.append(", ");
        if (username != null) {
            builder.append("username=");
            builder.append(username);
            builder.append(", ");
        }
        if (password != null) {
            builder.append("password=");
            builder.append(password);
            builder.append(", ");
        }
        if (mode != null) {
            builder.append("mode=");
            builder.append(mode);
            builder.append(", ");
        }
        if (comment != null) {
            builder.append("comment=");
            builder.append(comment);
            builder.append(", ");
        }
        if (verifyMethod != null) {
            builder.append("verifyMethod=");
            builder.append(verifyMethod);
            builder.append(", ");
        }
        builder.append("status=");
        builder.append(status);
        builder.append(", ");
        if (url != null) {
            builder.append("url=");
            builder.append(url);
            builder.append(", ");
        }
        if (rootPath != null) {
            builder.append("rootPath=");
            builder.append(rootPath);
            builder.append(", ");
        }
        builder.append("type=");
        builder.append(type);
        builder.append("]");
        return builder.toString();
    }

}

package com.cnscud.xpower.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.function.Function;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.HttpContext;

/**
 * HTTP 响应（包装结果）
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2013-06-06
 */
public class Response {
    private String _entity;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream(0);
    private Charset encoding = Charsets.UTF_8;
    private Header[] headers;
    private HttpContext httpContext;
    int statusCode;
    long timecost;

    //
    public Response() {
    }

    public Response(int statusCode) {
        this.statusCode = statusCode;
    }

    public byte[] asBytes() {
        return baos.toByteArray();
    }

    /**
     * 随机生成文件名称
     * 
     * @return 具体的文件路径
     * @throws IOException
     */
    public File asFile() throws IOException {
        File file = File.createTempFile(UUID.randomUUID().toString(), "");
        return asFile(file);
    }

    public File asFile(File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            baos.writeTo(fos);
        }
        return file;
    }

    public <T> T asJson(Function<String, T> f) {
        return f.apply(asString());
    }

    public OutputStream asOutputStream() {
        return baos;
    }

    public String asString() {
        return asString(this.encoding);
    }
    
    public String asString(Charset encoding) {
        if (_entity == null || !this.encoding.equals(encoding)) {
            _entity = new String(baos.toByteArray(), encoding);
        }
        return _entity;
    }

    void copyStream(HttpEntity entity) throws IOException {
        InputStream in = entity.getContent();
        if (in == null)
            return;
        try {
            long size = entity.getContentLength();
            if (size > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("HTTP entity too large to be buffered in memory: " + size);
            }
            size = Math.max(4096, size);
            baos = new ByteArrayOutputStream((int) size);
            int len;

            byte[] b = new byte[4096];
            while ((len = in.read(b)) > 0) {
                baos.write(b, 0, len);
            }
        } finally {
            in.close();
        }
    }

    public Cookie getCookie(String name) {
        CookieStore cookieStore = getCookieStore();
        if (cookieStore != null) {
            for (Cookie cookie : cookieStore.getCookies()) {
                if (cookie.getName().equalsIgnoreCase(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }

    public CookieStore getCookieStore() {
        if (httpContext == null)
            return null;
        CookieStore cookieStore;
        if (httpContext instanceof HttpClientContext) {
            cookieStore = ((HttpClientContext) httpContext).getCookieStore();
        } else {
            cookieStore = (CookieStore) httpContext.getAttribute(HttpClientContext.COOKIE_STORE);
        }

        if (cookieStore != null) {
            // copy to prevent concurrence access and memory leak
            BasicCookieStore basicCookieStore = new BasicCookieStore();
            for (Cookie cookie : cookieStore.getCookies()) {
                basicCookieStore.addCookie(cookie);
            }
            cookieStore = basicCookieStore;
        }
        return cookieStore;
    }

    public String getCookieValue(String name) {
        Cookie cookie = getCookie(name);
        return cookie != null ? cookie.getValue() : null;
    }

    public Header getHeader(String name) {
        if (headers != null) {
            for (Header h : headers) {
                if (h.getName().equals(name)) {
                    return h;
                }
            }
        }
        return null;
    }

    public Header[] getHeaders() {
        return this.headers;
    }

    public String getHeaderValue(String name) {
        Header h = getHeader(name);
        return h == null ? null : h.getValue();
    }

    public HttpContext getHttpContext() {
        return this.httpContext;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public long getTimecost() {
        return this.timecost;
    }

    public boolean isScOk() {
        return statusCode == HttpStatus.SC_OK;
    }

    public Response setEncoding(Charset encoding) {
        this.encoding = encoding;
        return this;
    }

    void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    void setHttpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
    }

    @Override
    public String toString() {
        return "Response{" + "statusCode=" + statusCode + ", timecost=" + timecost + '}';
    }
}

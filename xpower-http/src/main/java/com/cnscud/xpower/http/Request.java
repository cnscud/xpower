package com.cnscud.xpower.http;

import static java.lang.String.format;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

/**
 * HTTP请求（用于包装参数）
 * 
 * @author adyliu (imxylz@gmail.com)
 * @since 2013-06-06
 */
public class Request {
    final static String localSiteAddress = _calcAllLocalSiteAddress();
    /**
     * 获取所有私有地址信息（不包括公网IP地址信息），以'-'分隔多个私有地址，不包括回环地址。当且仅当无法获取私有地址时返回回环地址( 127.0.0.1)
     * <p>
     * 私有地址包括如下三类：
     * <ul>
     * <li>A类 10.0.0.0 --10.255.255.255</li>
     * <li>B类 172.16.0.0--172.31.255.255</li>
     * <li>C类 192.168.0.0--192.168.255.255</li>
     * </ul>
     * 忽略IPv6的所有地址
     * </p>
     * 
     * @return 所有私有地址信息
     */
    private static String _calcAllLocalSiteAddress() {
        String addrs = "";
        try {
            for (NetworkInterface ifc : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (ifc.isUp() && !ifc.isLoopback()) {
                    for (InetAddress addr : Collections.list(ifc.getInetAddresses())) {
                        if (addr instanceof Inet4Address && addr.isSiteLocalAddress()) {// 私有地址
                            addrs += "-" + addr.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception e) {
            // ignore all exception
        }
        return addrs.length() > 0 ? addrs.substring(1) : "unknown";
    }
    public static String buildUrl(String uri, Charset encoding, List<? extends NameValuePair> params) {
        if (params == null || params.isEmpty()) {
            return uri;
        }
        String queryString = URLEncodedUtils.format(params, encoding);
        uri += uri.indexOf('?') > -1 ? "&" : "?";
        return uri + queryString;
    }
    public static String buildUrl(String uri, Charset encoding, Map<String, String> params) {
        List<StringPair> list = new ArrayList<StringPair>(params.size());
        for (Map.Entry<String, String> entry : params.entrySet()) {
            list.add(new StringPair(entry.getKey(), entry.getValue()));
        }
        return buildUrl(uri, encoding, list);
    }
    
    
    Charset encoding = Charsets.UTF_8;
    private HttpEntity entity;
    /**
     * 是否强制状态为SC_OK=200，如果某个响应状态码不是200，则会抛出IOException， forceOk能抑制这种行为。
     */
    boolean forceOk = true;
    private List<Header> headers = new ArrayList<Header>();
    private HttpClientContext httpContext = new HttpClientContext();
    String method = "GET";

    private List<StringPair> params = new ArrayList<StringPair>();

    private RequestConfig.Builder requestConfig = Http.createRequestConfig();

    int retry = 0;

    private String uri;

    //
    public Request(String uri) {
        this(uri, "GET");
    }

    public Request(String uri, String method) {
        this(uri, method, Charsets.UTF_8);
    }

    /**
     * 构造一个http 请求
     * 
     * @param uri
     *            请求的地址
     * @param method
     *            请求方法字符串
     * @param encoding
     *            字符编码，请求内容的字符编码
     */
    public Request(String uri, String method, Charset encoding) {
        this.uri = uri;
        this.method = method;
        this.encoding = encoding;
        this.setUserAgent(format("httpclient by panda.com (imxylz@gmail.com, %s)", localSiteAddress));
    }

    /**
     * 请求增加Cookie
     * 
     * @param name
     *            cookie名称filem
     * @param value
     *            cookie值
     * @return 请求对象
     */
    public Request addCookie(String name, String value, String domain, String path, Date expiryDate, int version) {
        if (getHttpContext().getCookieStore() == null) {
            withCookieStore();
        }
        BasicClientCookie cookie = new BasicClientCookie(name, value);
        cookie.setExpiryDate(expiryDate);
        cookie.setDomain(domain);
        cookie.setPath(path);
        cookie.setSecure(isSecurity());
        cookie.setVersion(version);
        getHttpContext().getCookieStore().addCookie(cookie);
        return this;
    }

    public Request addHeaders(Header... headers) {
        for (Header header : headers) {
            this.headers.add(header);
        }
        return this;
    }

    public Request addHeaders(String... headers) {
        if (headers.length % 2 != 0) {
            throw new IllegalArgumentException("error headers. " + Arrays.toString(headers));
        }
        for (int i = 0; i < headers.length; i += 2) {
            addHeaders(new BasicHeader(headers[i], headers[i + 1]));
        }
        return this;
    }

    /**
     * 添加一个参数
     * 
     * @param name
     *            参数名
     * @param value
     *            参数值
     * @return 如果参数值不为null,则将此参数添加到QueryString/RequestBody中
     * @since 2016年11月25日
     * @author Ady Liu (imxylz@gmail.com)
     */
    public Request addParam(String name, Object value) {
        return value != null ? addParams(name, String.valueOf(value)) : this;
    }

    public Request addParams(Collection<String> params) {
        return addParams(params.toArray(new String[params.size()]));
    }

    public Request addParams(List<StringPair> params) {
        this.params.addAll(params);
        return this;
    }

    public Request addParams(Map<String, String> params) {
        for (Map.Entry<String, String> e : params.entrySet()) {
            this.params.add(new StringPair(e.getKey(), e.getValue()));
        }
        return this;
    }

    public Request addParams(String... params) {
        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("error params. " + Arrays.toString(params));
        }
        for (int i = 0; i < params.length; i += 2) {
            addParams(new StringPair(params[i], params[i + 1]));
        }
        return this;
    }

    public Request addParams(StringPair... params) {
        for (StringPair param : params) {
            this.params.add(param);
        }
        return this;
    }

    public Response execute() throws IOException {
        return Http.execute(this);
    }

    public Response get() throws IOException {
        setMethod("GET");
        return execute();
    }

    public HttpEntity getEntity() {
        if (entity != null) {
            return entity;
        }
        if (!params.isEmpty()) {
            return new UrlEncodedFormEntity(params, encoding);
        }
        return null;
    }

    public Header[] getHeaders() {
        return headers.toArray(new Header[headers.size()]);
    }

    public HttpClientContext getHttpContext() {
        return this.httpContext;
    }

    /** 获取本机的全部私有IP地址 */
    public String getLocalsiteaddress() {
        return localSiteAddress;
    }

    public List<StringPair> getParams() {
        return this.params;
    }

    /**
     * 拼接完整的URL（如果是GET请求，则将全部参数拼接到URL上，POST请求并不会）
     * 
     * @return 完整的URL，含QueryString
     */
    public String getUrl() {
        if (isGet()) {
            return buildUrl(uri, encoding, params);
        }
        return uri;
    }

    public boolean isGet() {
        return "GET".equalsIgnoreCase(method);
    }

    public boolean isPost() {
        return "POST".equalsIgnoreCase(method);
    }

    private boolean isSecurity() {
        return this.uri.toLowerCase().startsWith("https://");
    }

    public Response post() throws IOException {
        setMethod("POST");
        return execute();
    }

    void prepare(HttpRequestBase requestBase) {
        for (Header header : getHeaders()) {
            requestBase.addHeader(header);
        }
        if (requestBase instanceof HttpPost) {
            HttpPost post = (HttpPost) requestBase;
            post.setEntity(getEntity());
        }
        if (requestBase instanceof HttpPut) {
            HttpPut put = (HttpPut) requestBase;
            put.setEntity(getEntity());
        }
        requestBase.setConfig(requestConfig.build());
    }

    public Request removeHeader(String name) {
        for (Iterator<Header> headers = this.headers.iterator(); headers.hasNext();) {
            if (headers.next().getName().equalsIgnoreCase(name)) {
                headers.remove();
            }
        }
        return this;
    }
    /**
     * 出错尝试几次
     * 
     * @param count
     *            尝试次数，默认为0，不尝试
     * @return Request 对象
     */
    public Request retry(int count) {
        this.retry = count;
        return this;
    }

    /**
     * set the connection timeout
     * 
     * @param timeout
     *            the timeout value to be used in milliseconds.
     * @return the request object
     */
    public Request setConnectionTimeout(int timeout) {
        requestConfig.setConnectTimeout(timeout);
        return this;
    }

    public Request setCookieStore(CookieStore cookieStore) {
        getHttpContext().setCookieStore(cookieStore);
        return this;
    }

    public Request setEncoding(Charset encoding) {
        this.encoding = encoding;
        return this;
    }

    public Request setEntity(HttpEntity entity) {
        this.entity = entity;
        return this;
    }

    /**
     * 设置请求的内容体（注意，此方法和{@link #addParams(String...)}等addParams* 方法有冲突，二选一）
     * 
     * @param content
     *            请求内容（使用{@link #setEncoding(Charset)}编码
     * @return Request请求
     * @since 2013/09/16
     */
    public Request setEntity(String content) {
        return setEntity(new StringEntity(content, encoding));
    }

    /**
     * 增加强制状态是否为 200 控制响应码不是200的正确返回
     * @param forceOk
     * @return
     */
    public Request setForceOk(boolean forceOk) {
        this.forceOk = forceOk;
        return this;
    }

    public Request setMethod(String method) {
        this.method = method;
        return this;
    }

    /**
     * 用uri来构造Referer
     * 
     * @return Request对象
     * @see #setReferer(String)
     */
    public Request setReferer() {
        return setReferer(uri);
    }

    public Request setReferer(String referer) {
        return removeHeader(HttpHeaders.REFERER).addHeaders(HttpHeaders.REFERER, referer);
    }

    /**
     * set the so_timeout
     * 
     * @param timeout
     *            the timeout value to be used in milliseconds
     * @return the request object
     */
    public Request setSoTimeout(int timeout) {
        requestConfig.setSocketTimeout(timeout);
        return this;
    }

    /**设置连接或者请求超时*/
    public Request setTimeout(int connectionTimeoutInMillSeconds, int soTimeoutInMillSeconds) {
        return setConnectionTimeout(connectionTimeoutInMillSeconds).setSoTimeout(soTimeoutInMillSeconds);
    }

    /** 使用Chrome浏览器模拟 */
    public Request setUserAgent() {
        return setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
    }

    /**
     * 设置HTTP的UserAgent对象
     * 
     * @param userAgent
     *            useragent格式
     * @return Request对象
     * @see #setUserAgent()
     */
    public Request setUserAgent(String userAgent) {
        return removeHeader(HttpHeaders.USER_AGENT).addHeaders(HttpHeaders.USER_AGENT, userAgent);
    }

    /**
     * 生成调试信息（包括HEADER/BODY等）
     * 
     * @return HTTP请求的调试信息
     */
    public String toDebugString() throws IOException {
        StringBuilder buf = new StringBuilder(1024);
        buf.append(format("%s %s\n", method, uri));
        if (!headers.isEmpty()) {
            buf.append("HEADERS: ");
            for (Header header : headers) {
                buf.append(format("%s=%s\n", header.getName(), header.getValue()));
            }
        }
        if (isGet()) {
            buf.append(format("URL: %s\n", buildUrl(uri, encoding, getParams())));
        } else if (isPost()) {
            HttpEntity e = getEntity();
            if (e != null) {
                buf.append(EntityUtils.toString(e, encoding));
            }
        }
        return buf.toString();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Request{");
        sb.append("uri='").append(uri).append('\'');
        sb.append(", encoding=").append(encoding);
        sb.append(", method='").append(method).append('\'');
        sb.append(", httpParams=").append(requestConfig.toString());
        sb.append('}');
        return sb.toString();
    }

    public Request withCookieStore() {
        return setCookieStore(new BasicCookieStore());
    }

    public Request withHttpProxy(HttpHost host) {
        requestConfig.setProxy(host);
        return this;
    }

    public Request withHttpProxy(String host, int port) {
        return withHttpProxy(new HttpHost(host, port));
    }
    
    public Request setRedirectsEnabled(boolean redirectsEnabled) {
        if(!redirectsEnabled) {
            setForceOk(false);//如果开启了不跳转，对于301、302那么不判断为错误
        }
        requestConfig.setRedirectsEnabled(redirectsEnabled);
        return this;
    }
}

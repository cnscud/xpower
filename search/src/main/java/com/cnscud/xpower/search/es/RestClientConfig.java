package com.cnscud.xpower.search.es;

import com.cnscud.xpower.configcenter.ConfigCenter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Deacon Peng 2020-11-13 15:27
 * @version 1.0.0
 */
@Slf4j
public class RestClientConfig {

    private static final int ADDRESS_LENGTH = 2;
    private static final String HTTP_SCHEME = "http";


    private RestClientBuilder restClientBuilder() {
        String[] ipAddress = ConfigCenter.getInstance()
                .getDataAsString("/xpower/config/elasticsearch.ip").split(",");
        HttpHost[] hosts = Arrays.stream(ipAddress)
                .map(this::makeHttpHost)
                .filter(Objects::nonNull)
                .toArray(HttpHost[]::new);
        log.info("hosts:{}", Arrays.toString(hosts));
        return RestClient.builder(hosts);
    }


    RestHighLevelClient client() {
        RestClientBuilder restClientBuilder = restClientBuilder();
        restClientBuilder.setMaxRetryTimeoutMillis(60000);
        restClientBuilder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(5000);
            requestConfigBuilder.setSocketTimeout(30000);
            requestConfigBuilder.setConnectionRequestTimeout(1000);
            return requestConfigBuilder;
        });
        // 异步httpclient连接数配置
        restClientBuilder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(100);
            httpClientBuilder.setMaxConnPerRoute(100);
            return httpClientBuilder;
        });
        return new RestHighLevelClient(restClientBuilder);
    }



    private HttpHost makeHttpHost(String s) {
        assert StringUtils.isNotEmpty(s);
        String[] address = s.split(":");
        if (address.length == ADDRESS_LENGTH) {
            String ip = address[0];
            int port = Integer.parseInt(address[1]);
            return new HttpHost(ip, port, HTTP_SCHEME);
        } else {
            return null;
        }
    }


}

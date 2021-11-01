package com.netease.http;

import com.netease.util.gson.GsonUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AppHttpClient {

    private static final Logger logger = LoggerFactory.getLogger(AppHttpClient.class);

    @Resource
    private HttpClient httpClient;

    @Resource
    private RequestConfig defaultRequestConfig;

    public AppHttpClient() {
    }

    /**
     * 发起 HTTP 请求
     *
     * @param request 请求
     * @return HTTP 响应
     * @throws IOException 异常
     */
    public HttpResp executeRequest(HttpUriRequest request) throws IOException {

        logRequest(request);

        CloseableHttpResponse response = (CloseableHttpResponse) httpClient.execute(request);
        request.addHeader(HTTP.USER_AGENT, "Netease1.0");
        long watch = System.nanoTime();
        HttpResp httpResp = new HttpResp();

        try {
            int statusCode = response.getStatusLine().getStatusCode();
            String body = null;
            if (statusCode == HttpStatus.SC_OK) {
                body = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            }
            int httpCode = response.getStatusLine().getStatusCode();
            String httpDesc = response.getStatusLine().getReasonPhrase();
            httpResp.setBody(body);
            httpResp.setHttpCode(httpCode);
            httpResp.setHttpDesc(httpDesc);
            logger.info("http execute successfully, uri={}, statusCode={}", request.getURI(), statusCode);
            EntityUtils.consumeQuietly(response.getEntity());
            return httpResp;
        } catch (Exception e) {
            logger.error("请求失败", e);
            throw e;
        } finally {
            IOUtils.closeQuietly(response);
            if (logger.isDebugEnabled())
                logger.debug("fetch result: url: {}, httpCode: {}, httpDesc: {}, body: {}, consume: {}", request.getURI(), httpResp.getHttpCode(), httpResp.getHttpDesc(), httpResp.getBody(), (System.nanoTime() - watch) / 1000);
        }
    }

    /**
     * 发起自定义超时时间的请求
     *
     * @param request 请求
     * @param timeout 超时时间
     * @return 请求响应
     * @throws IOException 异常
     */
    public String executeRequestWithTimeout(HttpUriRequest request, int timeout) throws IOException {
        HttpResp httpResp = executeRequest(
                RequestBuilder.copy(request)
                        .setConfig(RequestConfig.copy(defaultRequestConfig).setSocketTimeout(timeout).build())
                        .build());

        return httpResp.getBody();
    }

    /**
     * 打印请求
     *
     * @param request 请求
     */
    private void logRequest(HttpUriRequest request) {

        try {
            String url = request.getURI().toString();

            String requestBody = "";
            if (request instanceof HttpEntityEnclosingRequestBase) {
                HttpEntityEnclosingRequestBase requestBase = (HttpEntityEnclosingRequestBase) request;
                HttpEntity entity = requestBase.getEntity();
                if (entity != null) {
                    requestBody = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
                }
            }

            Map<String, Object> headerMap = new HashMap<>();
            for (Header header : request.getAllHeaders()) {
                headerMap.put(header.getName(), header.getValue());
            }

            if (logger.isDebugEnabled()) {
                logger.debug("fetch request url: {}, method: {}, body: {}, header: {}",
                        url, request.getMethod(), requestBody, GsonUtil.toJson(headerMap));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}

package com.netease.edu.sample.configuration;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Created by yuyang04 on 2021/2/20.
 */
public class AppHttpRetryHandler implements HttpRequestRetryHandler {

    private final int maxRetry;

    public AppHttpRetryHandler() {
        maxRetry = 3;
    }

    public AppHttpRetryHandler(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        return exception instanceof NoHttpResponseException && executionCount < maxRetry;
    }
}

package com.netease.edu.sample.service.impl;

import com.netease.edu.sample.service.HttpClientService;
import com.netease.http.AppHttpClient;
import com.netease.http.HttpClientUtil;
import com.netease.http.HttpResp;
import com.netease.util.CheckSumBuilder;
import com.netease.util.UUIDUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class HttpClientServiceImpl implements HttpClientService {
    @Resource
    private AppHttpClient appHttpClient;

    private void appendChecksum(HttpRequestBase req, String secret){
        String nonce = UUIDUtil.getUUID();
        String curTime = String.valueOf(System.currentTimeMillis() / 1000);
        HttpClientUtil.addHeader(req, "Nonce", nonce);
        HttpClientUtil.addHeader(req, "CurTime", curTime);
        String checksum = CheckSumBuilder.getCheckSum(nonce, curTime, secret);
        HttpClientUtil.addHeader(req, "CheckSum", checksum);
    }

    @Override
    public HttpResp post(String url, String appSecret, String body) throws IOException {
        HttpEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
        HttpPost post = HttpClientUtil.createPost(url, entity);
        appendChecksum(post, appSecret);
        return appHttpClient.executeRequest(post);
    }

    @Override
    public HttpResp put(String url, String appSecret, String body) throws IOException {
        HttpEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
        HttpPut post = HttpClientUtil.createPut(url, entity);
        appendChecksum(post, appSecret);
        return appHttpClient.executeRequest(post);
    }

    @Override
    public HttpResp get(String url, String appSecret) throws IOException {
        HttpGet get = HttpClientUtil.createGet(url, null, null);
        appendChecksum(get, appSecret);
        return appHttpClient.executeRequest(get);
    }

    @Override
    public HttpResp delete(String url, String appSecret, String body) throws IOException {
        HttpEntity entity = new StringEntity(body, ContentType.APPLICATION_JSON);
        HttpPost delete = HttpClientUtil.createDelete(url, entity, null);
        appendChecksum(delete, appSecret);
        return appHttpClient.executeRequest(delete);
    }
}

package com.netease.edu.sample.service;

import com.netease.http.HttpResp;

import java.io.IOException;
import java.util.Map;

public interface HttpClientService {
    HttpResp post(String url, String appSecret, String body) throws IOException;
    HttpResp put(String url, String appSecret, String body) throws IOException;
    HttpResp get(String url, String appSecret) throws IOException;
    HttpResp delete(String url, String appSecret, String body) throws IOException;
}

package com.netease.edu.sample.configuration;

import com.netease.http.AppHttpClient;
import com.netease.util.Conf;
import org.apache.http.Consts;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.CodingErrorAction;

@Configuration
public class AppHttpConfig {

    @Bean
    RequestConfig defaultRequestConfig(){
        return RequestConfig.custom()
                .setSocketTimeout(Conf.getInteger("HttpClientSocketTimeout", 5000))
                .setConnectTimeout(Conf.getInteger("HttpClientConnectTimeout", 5000))
                .setConnectionRequestTimeout(Conf.getInteger("HttpClientConnectionRequestTimeout", 5000))
                .build();
    }

    @Bean
    public HttpClient httpClient(){
        // default message constraints
        MessageConstraints defaultMessageConstraints = MessageConstraints.custom()
                .setMaxHeaderCount(Conf.getInteger("HttpClientMaxHeaderCount", 200))
                .setMaxLineLength(Conf.getInteger("HttpClientMaxLineLength", 2000))
                .build();

        // default connection config
        ConnectionConfig defaultConnectionConfig = ConnectionConfig.custom()
                .setMalformedInputAction(CodingErrorAction.IGNORE)
                .setUnmappableInputAction(CodingErrorAction.IGNORE)
                .setCharset(Consts.UTF_8)
                .setMessageConstraints(defaultMessageConstraints)
                .build();

        HttpClientBuilder hcb = HttpClients.custom();
        return hcb.setDefaultConnectionConfig(defaultConnectionConfig)
                .setDefaultRequestConfig(defaultRequestConfig())
                .setMaxConnTotal(Conf.getInteger("HttpClientMaxConnTotal", 800))
                .setMaxConnPerRoute(Conf.getInteger("HttpClientMaxConnPerRoute", 400))
                .setRetryHandler(new AppHttpRetryHandler(Conf.getInteger("HttpClientExceptionMaxRetry", 3)))
                .build();
    }
    @Bean
    public AppHttpClient appHttpClient() {
        return new AppHttpClient();
    }
}

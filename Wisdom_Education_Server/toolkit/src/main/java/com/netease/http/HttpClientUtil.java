package com.netease.http;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URLEncodedUtils;

import java.util.List;

public class HttpClientUtil {

    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String reqCharset = DEFAULT_CHARSET;

    public static void addHeader(HttpRequestBase request, String key, String val) {
        request.addHeader(key, val);
    }

    public static HttpPost createPost(String url, HttpEntity entity) {
        return createPost(url, entity, null);
    }

    public static HttpPost createPost(String url, HttpEntity entity, ApplicationType accept) {
        HttpPost method = new HttpPost(url);
        if (null != accept) {
            method.addHeader("accept", accept.val());
        }
        method.setEntity(entity);
        return method;
    }

    public static HttpPut createPut(String url, HttpEntity entity) {
        HttpPut method = new HttpPut(url);
        method.setEntity(entity);
        return method;
    }

    public static HttpPost createDelete(String url, HttpEntity entity, ApplicationType accept) {
        HttpPost method = new HttpPost(url){
            @Override
            public String getMethod() {
                return "DELETE";
            }
        };
        if (null != accept) {
            method.addHeader("accept", accept.val());
        }
        method.setEntity(entity);
        return method;
    }

    public static HttpGet createGet(String url, final List<NameValuePair> params, ApplicationType accept) {
        HttpGet method = new HttpGet(urlEncode(url, params));
        if (null != accept) {
            method.addHeader("accept", accept.val());
        }
        return method;
    }

    private static String urlEncode(String url, final List<NameValuePair> params) {
        if (params == null)
            return url;
        String param = URLEncodedUtils.format(params, reqCharset);
        if (!url.contains("?")) {
            url += "?" + param;
        } else {
            url += param;
        }
        return url;
    }

    public static HttpDelete createDelete(String url, final List<NameValuePair> params, ApplicationType accept) {
        HttpDelete method = new HttpDelete(urlEncode(url, params));
        if (null != accept) {
            method.addHeader("accept", accept.val());
        }
        return method;
    }

}

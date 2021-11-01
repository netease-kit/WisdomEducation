package com.netease.http;

public class HttpResp {
    private Integer httpCode;
    private String body;
    private String httpDesc;
    public HttpResp() {
    }

    public String getHttpDesc() {
        return httpDesc;
    }

    public void setHttpDesc(String httpDesc) {
        this.httpDesc = httpDesc;
    }

    public Integer getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(Integer httpCode) {
        this.httpCode = httpCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
package com.netease.edu.sample.service.impl;

import com.netease.edu.sample.parameter.UserCreateParam;
import com.netease.edu.sample.pojo.UserDto;
import com.netease.edu.sample.service.EduUserService;
import com.netease.edu.sample.service.HttpClientService;
import com.netease.http.HttpResp;
import com.netease.util.gson.GsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class EduUserServiceImpl implements EduUserService {
    @Value("${urlPutRoom:https://yiyong-xedu-v2.netease.im/apps/%s/v1/users/%s}")
    private String urlPutUser;
    @Value("${urlGetRoom:https://yiyong-xedu-v2.netease.im/apps/%s/v1/users/%s}")
    private String urlGetUser;
    @Resource
    private HttpClientService httpClientService;

    @Override
    public UserDto putUser(String appKey, String secret, String userUuid) throws IOException {
        return putUser(appKey, secret, userUuid, null, null);
    }

    @Override
    public UserDto putUser(String appKey, String secret, String userUuid, String userToken) throws IOException {
        return putUser(appKey, secret, userUuid, userToken, null);
    }

    @Override
    public UserDto putUser(String appKey, String secret, String userUuid, String userToken, String imToken) throws IOException {
        UserCreateParam body = new UserCreateParam();
        body.setUserToken(userToken);
        body.setImToken(imToken);
        return putUser(appKey, secret, userUuid, body);
    }

    @Override
    public UserDto putUser(String appKey, String secret, String userUuid, UserCreateParam userCreateParam) throws IOException {
        String url = String.format(urlPutUser, appKey, userUuid);
        HttpResp httpResp = httpClientService.put(url, secret, userCreateParam == null ? "{}" : GsonUtil.toJsonNoDouble(userCreateParam));
        if(httpResp.getHttpCode() == 200 || httpResp.getHttpCode() == 409){
            return GsonUtil.fromJson(httpResp.getBody(), UserDto.class);
        }
        return null;
    }

    @Override
    public UserDto getUser(String appKey, String secret, String userUuid) throws IOException {
        String url = String.format(urlGetUser, appKey, userUuid);
        HttpResp httpResp = httpClientService.get(url, secret);
        if(httpResp.getHttpCode() == 200 || httpResp.getHttpCode() == 409){
            return GsonUtil.fromJson(httpResp.getBody(), UserDto.class);
        }
        return null;
    }


}

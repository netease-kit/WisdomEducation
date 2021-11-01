package com.netease.edu.sample.service.impl;


import com.netease.edu.sample.parameter.PutRoomParam;
import com.netease.edu.sample.pojo.RoomDto;
import com.netease.edu.sample.service.EduRoomService;
import com.netease.edu.sample.service.HttpClientService;
import com.netease.http.HttpResp;
import com.netease.util.gson.GsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class EduRoomServiceImpl implements EduRoomService {
    @Value("${urlPutRoom:https://yiyong-xedu-v2.netease.im/apps/%s/v1/rooms/%s}")
    private String urlPutRoom;
    @Value("${urlPutRoom:https://yiyong-xedu-v2.netease.im/apps/%s/v1/rooms/%s}")
    private String urlDeleteRoom;
    @Value("${urlPutRoom:https://yiyong-xedu-v2.netease.im/apps/%s/v1/rooms/%s/members/%s}")
    private String urlDeleteRoomMember;
    @Resource
    private HttpClientService httpClientService;

    @Override
    public RoomDto putRoom(String appKey, String secret, String roomUuid, PutRoomParam putRoomParam) throws IOException {
        String url = String.format(urlPutRoom, appKey, roomUuid);
        String body = putRoomParam == null ? "{}" : GsonUtil.toJsonNoDouble(putRoomParam);
        HttpResp httpResp = httpClientService.put(url, secret, body);
        if(httpResp.getHttpCode() == 200 || httpResp.getHttpCode() == 409){
            return GsonUtil.fromJson(httpResp.getBody(), RoomDto.class);
        }
        return null;
    }

    @Override
    public boolean deleteRoom(String appKey, String secret, String roomUuid) throws IOException {
        String url = String.format(urlDeleteRoom, appKey, roomUuid);
        String body = "{}";
        HttpResp httpResp = httpClientService.delete(url, secret, body);
        return httpResp.getHttpCode() == 200;
    }

    @Override
    public boolean deleteMember(String appKey, String secret, String roomUuid, String memberUserUuid) throws IOException {
        String url = String.format(urlDeleteRoomMember, appKey, roomUuid, memberUserUuid);
        String body = "{}";
        HttpResp httpResp = httpClientService.delete(url, secret, body);
        return httpResp.getHttpCode() == 200;
    }
}

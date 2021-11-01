package com.netease.edu.sample.service;

import com.netease.edu.sample.parameter.PutRoomParam;
import com.netease.edu.sample.pojo.RoomDto;

import java.io.IOException;

public interface EduRoomService {
    RoomDto putRoom(String appKey, String secret, String roomUuid, PutRoomParam putRoomParam) throws IOException;
    boolean deleteRoom(String appKey, String secret, String roomUuid) throws IOException;
    boolean deleteMember(String appKey, String secret, String roomUuid, String memberUserUuid) throws IOException;
}

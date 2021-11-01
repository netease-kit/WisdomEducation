package com.netease.edu.sample.service;

import com.netease.edu.sample.parameter.UserCreateParam;
import com.netease.edu.sample.pojo.UserDto;

import java.io.IOException;

public interface EduUserService {
    UserDto putUser(String appKey, String secret, String userUuid) throws IOException;
    UserDto putUser(String appKey, String secret, String userUuid, String userToken) throws IOException;
    UserDto putUser(String appKey, String secret, String userUuid, String userToken, String imToken) throws IOException;
    UserDto putUser(String appKey, String secret, String userUuid, UserCreateParam userCreateDto) throws IOException;
    UserDto getUser(String appKey, String secret, String userUuid) throws IOException;
}

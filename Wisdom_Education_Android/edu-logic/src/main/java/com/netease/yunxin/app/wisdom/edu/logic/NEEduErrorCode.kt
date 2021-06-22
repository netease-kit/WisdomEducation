/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic

import com.netease.lava.nertc.sdk.NERtcConstants
import com.netease.yunxin.app.wisdom.im.IMErrorCode

/**
 * SDK通用错误码与错误描述
 */
enum class NEEduErrorCode(val code: Int, val msg: String) {

    // app error
    CLIENT_REQ_EXCEPTION(-3, "client exception"),

    CLIENT_EXCEPTION(-2, "client exception"),

    RTC_INIT_ERROR(-1, "Rtc init error"),

    SUCCESS(0, "Success"),

    /**
     * 参数非法
     */
    BAD_REQUEST(400, "Bad Request"),

    /**
     * 鉴权失败
     */
    UNAUTHORIZED(401, "Unauthorized"),

    /**
     * 房间操作权限禁止
     */
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),

    /**
     * method不支持
     */
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),

    /**
     * 创建房间时，房间号已经存在
     */
    CONFLICT(409, "Target Already Exists"),

    /**
     * 不支持的MediaType，比如非Json的body
     */
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),

    /**
     * 内部异常，一般是内部服务出现问题
     */
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    SERVICE_UNAVAILABLE(503, "Service Busy"),

    /**
     * 创建IM账户失败
     */
    NIM_USER_CREATE_ERROR(700, "Nim Create User Error"),

    /**
     * 指定IM账户不存在
     */
    NIM_USER_NOT_EXIST(701, "Nim User NOT exist"),

    /**
     * IM服务异常
     */
    NIM_SERVICE_ERROR(702, "Nim Bad Im Service"),

    /**
     * IM账户已存在
     */
    NIM_USER_EXIST(703, "Nim User exist"),

    /**
     * 房间内操作时，房间configId不存在，或者configId对应的config不存在，或有格式或内容有误；
     * 课堂开始 如step(1时，rtc房间未创建，因为此时录制没发开始
     */
    ROOM_NOT_PREPARED(1001, "Internal Server Error"),

    /**
     * 加入房间时，角色数量超限
     */
    ROOM_ROLE_EXCEED(1002, "Room Role Exceed"),

    /**
     * 加入房间时，指定角色未定义
     */
    ROOM_ROLE_UNDEFINED(1003, "Room Role Undefined"),

    /**
     * 任何和房间强关联的操作，指定的roomUuid查不到对应的活的房间
     */
    ROOM_NOT_EXIST(1004, "Room Not Found"),

    /**
     * 创建房间时，config不存在或无法使用
     */
    ROOM_BAD_CONFIG(1005, "Bad Room Config"),

    /**
     * 房间属性exclusive时put改属性，该属性已经存在
     */
    ROOM_PROPERTY_EXISTS(1006, "Property Exists"),

    /**
     * 成员属性exclusive时put改属性，该属性已经存在
     */
    ROOM_MEMBER_PROPERTY_EXISTS(1007, "Room Member Property Exists"),
    ROOM_SIT_CONFLICT(1008, "Room Sit Conflict"),
    ROOM_SIT_FULL(1009, "Room Sits Full"),
    ROOM_SIT_USER_CONFLICT(1010, "Room Sit User Conflict"),
    ROOM_SIT_NOT_EXIST(1011, "Room Sit Not Exist"),

    /**
     * put:member.stream时，该流的并发超限，并发超限，如屏幕共享同时只能一人
     */
    ROOM_STREAM_CONCURRENCY_OUT(1012, "Stream Exists"),

    /**
     * put:member.property时，该流的并发超限，并发超限, 如同时上台的数量只能有限个数
     */
    ROOM_PROPERTY_CONCURRENCY_OUT(1012, "Stream Out of Currency Limit"),

    //rtc error
    ENGINE_ERROR_FATAL(NERtcConstants.ErrorCode.ENGINE_ERROR_FATAL, ""),
    ENGINE_ERROR_OUT_OF_MEMORY(NERtcConstants.ErrorCode.ENGINE_ERROR_OUT_OF_MEMORY, ""),
    ENGINE_ERROR_INVALID_PARAM(NERtcConstants.ErrorCode.ENGINE_ERROR_INVALID_PARAM, ""),
    ENGINE_ERROR_NOT_SUPPORTED(NERtcConstants.ErrorCode.ENGINE_ERROR_NOT_SUPPORTED, ""),
    ENGINE_ERROR_INVALID_STATE(NERtcConstants.ErrorCode.ENGINE_ERROR_INVALID_STATE, ""),
    ENGINE_ERROR_LACK_OF_RESOURCE(NERtcConstants.ErrorCode.ENGINE_ERROR_LACK_OF_RESOURCE, ""),
    ENGINE_ERROR_INVALID_INDEX(NERtcConstants.ErrorCode.ENGINE_ERROR_INVALID_INDEX, ""),
    ENGINE_ERROR_DEVICE_NOT_FOUND(NERtcConstants.ErrorCode.ENGINE_ERROR_DEVICE_NOT_FOUND, ""),
    ENGINE_ERROR_INVALID_DEVICE_SOURCEID(NERtcConstants.ErrorCode.ENGINE_ERROR_INVALID_DEVICE_SOURCEID, ""),
    ENGINE_ERROR_INVALID_VIDEO_PROFILE(NERtcConstants.ErrorCode.ENGINE_ERROR_INVALID_VIDEO_PROFILE, ""),
    ENGINE_ERROR_CREATE_DEVICE_SOURCE_FAIL(NERtcConstants.ErrorCode.ENGINE_ERROR_CREATE_DEVICE_SOURCE_FAIL, ""),
    ENGINE_ERROR_INVALID_RENDER(NERtcConstants.ErrorCode.ENGINE_ERROR_INVALID_RENDER, ""),
    ENGINE_ERROR_DEVICE_PREVIEW_ALREADY_STARTED(NERtcConstants.ErrorCode.ENGINE_ERROR_DEVICE_PREVIEW_ALREADY_STARTED,
        ""),
    ENGINE_ERROR_TRANSMIT_PENDDING(NERtcConstants.ErrorCode.ENGINE_ERROR_TRANSMIT_PENDDING, ""),
    ENGINE_ERROR_CONNECT_FAIL(NERtcConstants.ErrorCode.ENGINE_ERROR_CONNECT_FAIL, ""),
    ENGINE_ERROR_CREATE_DUMP_FILE_FAIL(NERtcConstants.ErrorCode.ENGINE_ERROR_CREATE_DUMP_FILE_FAIL, ""),
    ENGINE_ERROR_START_DUMP_FAIL(NERtcConstants.ErrorCode.ENGINE_ERROR_START_DUMP_FAIL, ""),
    ENGINE_ERROR_ROOM_ALREADY_JOINED(NERtcConstants.ErrorCode.ENGINE_ERROR_ROOM_ALREADY_JOINED, ""),
    ENGINE_ERROR_ROOM_NOT_JOINED(NERtcConstants.ErrorCode.ENGINE_ERROR_ROOM_NOT_JOINED, ""),
    ENGINE_ERROR_ROOM_REPLEATEDLY_LEAVE(NERtcConstants.ErrorCode.ENGINE_ERROR_ROOM_REPLEATEDLY_LEAVE, ""),
    ENGINE_ERROR_REQUEST_JOIN_ROOM_FAIL(NERtcConstants.ErrorCode.ENGINE_ERROR_REQUEST_JOIN_ROOM_FAIL, ""),
    ENGINE_ERROR_SESSION_NOT_FOUND(NERtcConstants.ErrorCode.ENGINE_ERROR_SESSION_NOT_FOUND, ""),
    ENGINE_ERROR_USER_NOT_FOUND(NERtcConstants.ErrorCode.ENGINE_ERROR_USER_NOT_FOUND, ""),
    ENGINE_ERROR_INVALID_USERID(NERtcConstants.ErrorCode.ENGINE_ERROR_INVALID_USERID, ""),
    ENGINE_ERROR_MEDIA_NOT_STARTED(NERtcConstants.ErrorCode.ENGINE_ERROR_MEDIA_NOT_STARTED, ""),
    ENGINE_ERROR_SOURCE_NOT_FOUND(NERtcConstants.ErrorCode.ENGINE_ERROR_SOURCE_NOT_FOUND, ""),
    ENGINE_ERROR_CONNECTION_NOT_FOUND(NERtcConstants.ErrorCode.ENGINE_ERROR_CONNECTION_NOT_FOUND, ""),
    ENGINE_ERROR_STREAM_NOT_FOUND(NERtcConstants.ErrorCode.ENGINE_ERROR_STREAM_NOT_FOUND, ""),
    ENGINE_ERROR_ADD_TRACK_FAIL(NERtcConstants.ErrorCode.ENGINE_ERROR_ADD_TRACK_FAIL, ""),
    ENGINE_ERROR_TRACK_NOT_FOUND(NERtcConstants.ErrorCode.ENGINE_ERROR_TRACK_NOT_FOUND, ""),
    ENGINE_ERROR_MEDIA_CONNECTION_DISCONNECTED(NERtcConstants.ErrorCode.ENGINE_ERROR_MEDIA_CONNECTION_DISCONNECTED, ""),
    ENGINE_ERROR_SIGNAL_DISCONNECTED(NERtcConstants.ErrorCode.ENGINE_ERROR_SIGNAL_DISCONNECTED, ""),
    ENGINE_ERROR_SERVER_KICKED(NERtcConstants.ErrorCode.ENGINE_ERROR_SERVER_KICKED, ""),
    ENGINE_ERROR_ROOM_CLOSED(NERtcConstants.ErrorCode.ENGINE_ERROR_ROOM_CLOSED, ""),


    // IM error code, im 返回错误码统一加ERROR_CODE_BASE， 区分各种情况错误码
    IM_ERROR_CODE_BASE(IMErrorCode.ERROR_CODE_BASE.code, ""),

    /**
     * 被其他端的登录踢掉
     */
    KICKOUT(50007, ""),

    /**
     * 被同时在线的其他端主动踢掉
     */
    KICK_BY_OTHER_CLIENT(50008, ""),

    /**
     * 被服务器禁止登录
     */
    IM_FORBIDDEN(50009, ""),

    /**
     * 客户端版本错误
     */
    VER_ERROR(50010, ""),

    /**
     * 用户名或密码错误
     */
    PWD_ERROR(50011, ""),

}


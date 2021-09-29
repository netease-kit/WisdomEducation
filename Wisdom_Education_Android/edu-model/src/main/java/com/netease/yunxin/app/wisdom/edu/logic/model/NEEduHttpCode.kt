/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

/**
 * Created by hzsunyj on 2021/8/27.
 */
enum class NEEduHttpCode(val code: Int, val msg: String) {

    // app error
    IM_LOGIN_ERROR(-4, "im login error"),

    CLIENT_REQ_EXCEPTION(-3, "client exception"),

    CLIENT_EXCEPTION(-2, "client exception"),

    RTC_INIT_ERROR(-1, "Rtc init error"),

    SUCCESS(0, "Success"),

    /**
     * 无内容
     */
    NO_CONTENT(204, "No Content"),

    /**
     * 参数非法,xxx
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

    /**
     * 坐席配置不正确
     */
    ROOM_SITS_BAD(1014, "Room Sits Bad"),

    /**
     * 被操作的成员不存在
     */
    ROOM_DESTINATION_MEMBER_NOT_EXIST(1015, "Destination Member Server Error"),

    /**
     * 用户已在房间中
     */
    ROOM_MEMBER_EXIST(1016, "Member Exist"),

    /**
     * 创建房间时房间已经存在且config冲突
     *
     */
    ROOM_CONFIG_CONFLICT(1017, "Bad Room Config: Conflict"),
}

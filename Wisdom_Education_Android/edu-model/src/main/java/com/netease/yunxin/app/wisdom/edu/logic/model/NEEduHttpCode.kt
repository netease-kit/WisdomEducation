/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

/**
 * 
 */
enum class NEEduHttpCode(val code: Int, val msg: String) {

    // app error
    IM_LOGIN_ERROR(-4, "im login error"),

    CLIENT_REQ_EXCEPTION(-3, "client exception"),

    CLIENT_EXCEPTION(-2, "client exception"),

    RTC_INIT_ERROR(-1, "Rtc init error"),

    SUCCESS(0, "Success"),

    /**
     * No content
     */
    NO_CONTENT(204, "No Content"),

    /**
     * Invalid parameter, xxx
     */
    BAD_REQUEST(400, "Bad Request"),

    /**
     * Authentication failed
     */
    UNAUTHORIZED(401, "Unauthorized"),

    /**
     * Operation not allowed
     */
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),

    /**
     * The method is not supported
     */
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),

    /**
     * The room ID already exists
     */
    CONFLICT(409, "Target Already Exists"),

    /**
     * MediaType is not supported. For example, the body content is not in JSON format
     */
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),

    /**
     * Internal error
     */
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    SERVICE_UNAVAILABLE(503, "Service Busy"),

    /**
     * Failed to create the IM account
     */
    NIM_USER_CREATE_ERROR(700, "Nim Create User Error"),

    /**
     * The specified IM account does not exist
     */
    NIM_USER_NOT_EXIST(701, "Nim User NOT exist"),

    /**
     * IM service error
     */
    NIM_SERVICE_ERROR(702, "Nim Bad Im Service"),

    /**
     * The IM account already exists
     */
    NIM_USER_EXIST(703, "Nim User exist"),

    /**
     * The configId does not exist or the configuration with the configId does not exist. invalid format or incorrect content；
     * 
     */
    ROOM_NOT_PREPARED(1001, "Internal Server Error"),

    /**
     * The number of members exceeds the upper limit
     */
    ROOM_ROLE_EXCEED(1002, "Room Role Exceed"),

    /**
     * The specified role is undefined
     */
    ROOM_ROLE_UNDEFINED(1003, "Room Role Undefined"),

    /**
     * The room does not exist
     */
    ROOM_NOT_EXIST(1004, "Room Not Found"),

    /**
     * The configuration does not exist or is invalid when a room is created
     */
    ROOM_BAD_CONFIG(1005, "Bad Room Config"),

    /**
     * The room property already exists
     */
    ROOM_PROPERTY_EXISTS(1006, "Property Exists"),

    /**
     * The member property already exists
     */
    ROOM_MEMBER_PROPERTY_EXISTS(1007, "Room Member Property Exists"),
    ROOM_SIT_CONFLICT(1008, "Room Sit Conflict"),
    ROOM_SIT_FULL(1009, "Room Sits Full"),
    ROOM_SIT_USER_CONFLICT(1010, "Room Sit User Conflict"),
    ROOM_SIT_NOT_EXIST(1011, "Room Sit Not Exist"),

    /**
     * put:member.stream|property is the cocurrency limit of the attribute. For example, screen sharing is allowed for 1 member.
     */
    ROOM_MEMBER_CONCURRENCY_OUT(1012, "Member Property or Stream Out of Currency Limit"),

    /**
     * Invalid seating configuration
     */
    ROOM_SITS_BAD(1014, "Room Sits Bad"),

    /**
     * The destination member does not exist
     */
    ROOM_DESTINATION_MEMBER_NOT_EXIST(1015, "Destination Member Server Error"),

    /**
     * The member already exists
     */
    ROOM_MEMBER_EXIST(1016, "Member Exist"),

    /**
     * The room already exists and the configuration conflicts
     *
     */
    ROOM_CONFIG_CONFLICT(1017, "Bad Room Config: Conflict"),
}

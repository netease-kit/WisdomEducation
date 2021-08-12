/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic

import android.content.Context
import com.netease.lava.nertc.sdk.NERtcConstants
import com.netease.yunxin.app.wisdom.im.IMErrorCode

/**
 * SDK通用错误码与错误描述
 */
enum class NEEduErrorCode(val code: Int, val msg: String) {

    // app error
    IM_LOGIN_ERROR(-4, "im login error"),

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


    // IM error code, im 区分各种情况错误码
    /**
     * 被其他端的登录踢掉
     */
    KICKOUT(IMErrorCode.KICKOUT.code, ""),

    /**
     * 被同时在线的其他端主动踢掉
     */
    KICK_BY_OTHER_CLIENT(IMErrorCode.KICK_BY_OTHER_CLIENT.code, ""),

    /**
     * 被服务器禁止登录
     */
    IM_FORBIDDEN(IMErrorCode.IM_FORBIDDEN.code, ""),

    /**
     * 客户端版本错误
     */
    VER_ERROR(IMErrorCode.VER_ERROR.code, ""),

    /**
     * 用户名或密码错误
     */
    PWD_ERROR(IMErrorCode.PWD_ERROR.code, "");


    companion object {
        fun tipsWithErrorCode(context: Context, error: Int): String {
            return when (error) {
                IM_LOGIN_ERROR.code -> getString(context, R.string.im_login_error)
                RTC_INIT_ERROR.code -> getString(context, R.string.rtc_init_error)
                BAD_REQUEST.code -> getString(context, R.string.bad_request)
                UNAUTHORIZED.code -> getString(context, R.string.unauthorized)
                FORBIDDEN.code -> getString(context, R.string.forbidden)
                NOT_FOUND.code -> getString(context, R.string.not_found)
                METHOD_NOT_ALLOWED.code -> getString(context, R.string.method_not_allowed)
                CONFLICT.code -> getString(context, R.string.conflict)
                UNSUPPORTED_MEDIA_TYPE.code -> getString(context, R.string.unsupported_media_type)
                INTERNAL_SERVER_ERROR.code -> getString(context, R.string.internal_server_error)
                SERVICE_UNAVAILABLE.code -> getString(context, R.string.service_unavailable)
                ROOM_NOT_PREPARED.code -> getString(context, R.string.room_not_prepared)
                ROOM_ROLE_EXCEED.code -> getString(context, R.string.room_role_exceed)
                ROOM_ROLE_UNDEFINED.code -> getString(context, R.string.room_role_undefined)
                ROOM_NOT_EXIST.code -> getString(context, R.string.room_not_exist)
                ROOM_BAD_CONFIG.code -> getString(context, R.string.room_bad_config)
                ROOM_PROPERTY_EXISTS.code -> getString(context, R.string.room_property_exists)
                ROOM_MEMBER_PROPERTY_EXISTS.code -> getString(context, R.string.room_member_property_exists)
                ROOM_SIT_CONFLICT.code -> getString(context, R.string.room_sit_conflict)
                ROOM_SIT_FULL.code -> getString(context, R.string.room_sit_full)
                ROOM_SIT_USER_CONFLICT.code -> getString(context, R.string.room_sit_user_conflict)
                ROOM_SIT_NOT_EXIST.code -> getString(context, R.string.room_sit_not_exist)
                ROOM_STREAM_CONCURRENCY_OUT.code -> getString(context, R.string.room_stream_concurrency_out)
                ROOM_SITS_BAD.code -> getString(context, R.string.room_sits_bad)
                ROOM_DESTINATION_MEMBER_NOT_EXIST.code -> getString(context, R.string.room_destination_member_not_exist)
                ROOM_MEMBER_EXIST.code -> getString(context, R.string.room_member_exist)
                NIM_USER_CREATE_ERROR.code -> getString(context, R.string.nim_user_create_error)
                NIM_USER_NOT_EXIST.code -> getString(context, R.string.nim_user_not_exist)
                NIM_SERVICE_ERROR.code -> getString(context, R.string.nim_service_error)
                NIM_USER_EXIST.code -> getString(context, R.string.nim_user_exist)
                ENGINE_ERROR_CONNECT_FAIL.code -> getString(context, R.string.engine_error_connect_fail)
                ENGINE_ERROR_SERVER_KICKED.code -> getString(context, R.string.engine_error_server_kicked)
                ENGINE_ERROR_ROOM_CLOSED.code -> getString(context, R.string.engine_error_room_closed)
                else -> ""
            }
        }

        private fun getString(context: Context, resId: Int): String {
            return context.resources.getString(resId)
        }
    }
}


/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.im

import com.netease.nimlib.sdk.StatusCode
import com.netease.nimlib.sdk.chatroom.model.ChatRoomKickOutEvent

/**
 * 
 */
enum class IMErrorCode(val code: Int, val msg: String) {

    ERROR_CODE_BASE(50000, ""),
    CHAT_ERROR_CODE_BASE(60000, ""),
    KICKOUT(ERROR_CODE_BASE.code + StatusCode.KICKOUT.value, ""),
    KICK_BY_OTHER_CLIENT(ERROR_CODE_BASE.code + StatusCode.KICK_BY_OTHER_CLIENT.value, ""),
    IM_FORBIDDEN(ERROR_CODE_BASE.code + StatusCode.FORBIDDEN.value, ""),
    VER_ERROR(ERROR_CODE_BASE.code + StatusCode.VER_ERROR.value, ""),
    PWD_ERROR(ERROR_CODE_BASE.code + StatusCode.PWD_ERROR.value, ""),


    // chat room error
    KICK_OUT_BY_CONFLICT_LOGIN(CHAT_ERROR_CODE_BASE.code + ChatRoomKickOutEvent.ChatRoomKickOutReason
        .KICK_OUT_BY_CONFLICT_LOGIN.value, ""),
    KICK_OUT_BY_MANAGER(CHAT_ERROR_CODE_BASE.code + ChatRoomKickOutEvent.ChatRoomKickOutReason
        .KICK_OUT_BY_MANAGER.value, "");

    companion object {
        fun mapError(error: Int): Int {
            return ERROR_CODE_BASE.code + error
        }

        fun mapChatError(error: Int): Int {
            return CHAT_ERROR_CODE_BASE.code + error
        }
    }
}
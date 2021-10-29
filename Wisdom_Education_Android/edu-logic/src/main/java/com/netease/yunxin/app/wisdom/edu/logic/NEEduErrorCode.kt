/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic

import android.content.Context
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduHttpCode

/**
 * SDK通用错误码与错误描述
 */

class NEEduErrorCode {
    companion object {
        /**
         * Get tips with error code
         *
         * @param context
         * @param error error code value
         * @return
         */
        fun tipsWithErrorCode(context: Context, error: Int): String {
            return when (error) {
                NEEduHttpCode.IM_LOGIN_ERROR.code -> getString(context, R.string.im_login_error)
                NEEduHttpCode.RTC_INIT_ERROR.code -> getString(context, R.string.rtc_init_error)
                NEEduHttpCode.BAD_REQUEST.code -> getString(context, R.string.bad_request)
                NEEduHttpCode.UNAUTHORIZED.code -> getString(context, R.string.unauthorized)
                NEEduHttpCode.FORBIDDEN.code -> getString(context, R.string.forbidden)
                NEEduHttpCode.NOT_FOUND.code -> getString(context, R.string.not_found)
                NEEduHttpCode.METHOD_NOT_ALLOWED.code -> getString(context, R.string.method_not_allowed)
                NEEduHttpCode.CONFLICT.code -> getString(context, R.string.conflict)
                NEEduHttpCode.UNSUPPORTED_MEDIA_TYPE.code -> getString(context, R.string.unsupported_media_type)
                NEEduHttpCode.INTERNAL_SERVER_ERROR.code -> getString(context, R.string.internal_server_error)
                NEEduHttpCode.SERVICE_UNAVAILABLE.code -> getString(context, R.string.service_unavailable)
                NEEduHttpCode.ROOM_NOT_PREPARED.code -> getString(context, R.string.room_not_prepared)
                NEEduHttpCode.ROOM_ROLE_EXCEED.code -> getString(context, R.string.room_role_exceed)
                NEEduHttpCode.ROOM_ROLE_UNDEFINED.code -> getString(context, R.string.room_role_undefined)
                NEEduHttpCode.ROOM_NOT_EXIST.code -> getString(context, R.string.room_not_exist)
                NEEduHttpCode.ROOM_BAD_CONFIG.code -> getString(context, R.string.room_bad_config)
                NEEduHttpCode.ROOM_PROPERTY_EXISTS.code -> getString(context, R.string.room_property_exists)
                NEEduHttpCode.ROOM_MEMBER_PROPERTY_EXISTS.code -> getString(context,
                    R.string.room_member_property_exists)
                NEEduHttpCode.ROOM_SIT_CONFLICT.code -> getString(context, R.string.room_sit_conflict)
                NEEduHttpCode.ROOM_SIT_FULL.code -> getString(context, R.string.room_sit_full)
                NEEduHttpCode.ROOM_SIT_USER_CONFLICT.code -> getString(context, R.string.room_sit_user_conflict)
                NEEduHttpCode.ROOM_SIT_NOT_EXIST.code -> getString(context, R.string.room_sit_not_exist)
                NEEduHttpCode.ROOM_MEMBER_CONCURRENCY_OUT.code -> getString(context,
                    R.string.room_stream_concurrency_out)
                NEEduHttpCode.ROOM_SITS_BAD.code -> getString(context, R.string.room_sits_bad)
                NEEduHttpCode.ROOM_DESTINATION_MEMBER_NOT_EXIST.code -> getString(context,
                    R.string.room_destination_member_not_exist)
                NEEduHttpCode.ROOM_MEMBER_EXIST.code -> getString(context, R.string.room_member_exist)
                NEEduHttpCode.ROOM_CONFIG_CONFLICT.code -> getString(context, R.string.room_config_conflict)
                NEEduHttpCode.NIM_USER_CREATE_ERROR.code -> getString(context, R.string.nim_user_create_error)
                NEEduHttpCode.NIM_USER_NOT_EXIST.code -> getString(context, R.string.nim_user_not_exist)
                NEEduHttpCode.NIM_SERVICE_ERROR.code -> getString(context, R.string.nim_service_error)
                NEEduHttpCode.NIM_USER_EXIST.code -> getString(context, R.string.nim_user_exist)
                NEEduRtcCode.ENGINE_ERROR_CONNECT_FAIL.code -> getString(context, R.string.engine_error_connect_fail)
                NEEduRtcCode.ENGINE_ERROR_SERVER_KICKED.code -> getString(context, R.string.engine_error_server_kicked)
                NEEduRtcCode.ENGINE_ERROR_ROOM_CLOSED.code -> getString(context, R.string.engine_error_room_closed)
                NEEduIMCode.KICK_OUT_BY_CONFLICT_LOGIN.code -> getString(context, R.string.account_relogged_in_elsewhere)
                else -> ""
            }
        }

        private fun getString(context: Context, resId: Int): String {
            return context.resources.getString(resId)
        }
    }
}


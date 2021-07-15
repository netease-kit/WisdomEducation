/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.im

import com.netease.nimlib.sdk.StatusCode

/**
 * Created by hzsunyj on 2021/6/4.
 */
enum class IMErrorCode(val code: Int, val msg: String) {

    ERROR_CODE_BASE(50000, ""),
    KICKOUT(ERROR_CODE_BASE.code + StatusCode.KICKOUT.value, ""),
    KICK_BY_OTHER_CLIENT(ERROR_CODE_BASE.code + StatusCode.KICK_BY_OTHER_CLIENT.value, ""),
    IM_FORBIDDEN(ERROR_CODE_BASE.code + StatusCode.FORBIDDEN.value, ""),
    VER_ERROR(ERROR_CODE_BASE.code + StatusCode.VER_ERROR.value, ""),
    PWD_ERROR(ERROR_CODE_BASE.code + StatusCode.PWD_ERROR.value, "");

    companion object{
        fun mapError(error: Int): Int {
            return ERROR_CODE_BASE.code + error
        }
    }
}
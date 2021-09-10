/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic

import com.netease.yunxin.app.wisdom.im.IMErrorCode

/**
 * Created by hzsunyj on 2021/8/27.
 */
enum class NEEduIMCode(val code: Int, val msg: String) {
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


}
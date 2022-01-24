/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic

import com.netease.yunxin.app.wisdom.im.IMErrorCode

/**
 * 
 */
enum class NEEduIMCode(val code: Int, val msg: String) {
    // IM error codes
    /**
     * Login is invalidated by simultaneous logins on other clients using the same credentials
     */
    KICKOUT(IMErrorCode.KICKOUT.code, ""),

    /**
     * Login is invalidated by simultaneous logins on other clients using the same credentials
     */
    KICK_BY_OTHER_CLIENT(IMErrorCode.KICK_BY_OTHER_CLIENT.code, ""),

    /**
     * Login banned on the server
     */
    IM_FORBIDDEN(IMErrorCode.IM_FORBIDDEN.code, ""),

    /**
     * Client version error
     */
    VER_ERROR(IMErrorCode.VER_ERROR.code, ""),

    /**
     * Incorrect username and password
     */
    PWD_ERROR(IMErrorCode.PWD_ERROR.code, ""),

    /**
     * Login is invalidated by simultaneous logins on other clients using the same credentials
     */
    KICK_OUT_BY_CONFLICT_LOGIN(IMErrorCode.KICK_OUT_BY_CONFLICT_LOGIN.code, ""),

    /**
     * Login is banned by admins
     */
    KICK_OUT_BY_MANAGER(IMErrorCode.KICK_OUT_BY_MANAGER.code, "");

}
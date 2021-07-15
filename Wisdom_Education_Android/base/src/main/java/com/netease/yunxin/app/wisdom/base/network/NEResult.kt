/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.base.network

/**
 * Created by hzsunyj on 4/20/21.
 */
open class NEResult<T>(
    val code: Int,
    val requestId: String?,
    val msg: String?,
    val ts: Long? = 0,
    val data: T?,
) {

    constructor(code: Int) : this(code, "0")

    constructor(code: Int, data: T?) : this(code, "0", null, 0L, data)

    constructor(code: Int, requestId: String?) : this(code, requestId, null)

    constructor(code: Int, requestId: String?, msg: String?) : this(code, requestId, msg, 0L, null)

    fun success() = code == ErrorCode.ok

    fun success(alias: Int) = code == alias

    override fun toString(): String {
        return "code= $code requestId=$requestId msg=$msg ts=$ts"
    }
}
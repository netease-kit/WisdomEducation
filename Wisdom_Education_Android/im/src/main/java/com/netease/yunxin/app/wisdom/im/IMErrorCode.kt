/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.im

/**
 * Created by hzsunyj on 2021/6/4.
 */
enum class IMErrorCode(val code: Int, val msg: String) {
    ERROR_CODE_BASE(50000, ""),

    // 透传默认将所有错误码都add 16000， 因此实际错误码需要减去base值
    PASSTHROUGH_ERROR_CODE_BASE(16000, "")
}
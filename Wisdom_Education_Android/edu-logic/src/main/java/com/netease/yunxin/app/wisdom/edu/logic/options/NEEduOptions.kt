/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.options

import com.netease.yunxin.app.wisdom.edu.logic.foreground.NEEduForegroundServiceConfig

/**
 * Created by hzsunyj on 4/21/21.
 */
class NEEduOptions(
    val appKey: String,
    val baseUrl: String,
    val reuseIM: Boolean? = false,
    val foregroundServiceConfig: NEEduForegroundServiceConfig? = null,
)
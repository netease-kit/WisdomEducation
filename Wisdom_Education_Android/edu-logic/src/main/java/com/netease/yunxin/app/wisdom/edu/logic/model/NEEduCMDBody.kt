/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

/**
 * Created by hzsunyj on 2021/5/17.
 */
class NEEduCMDBody(
    val cmd: Int,
    val sequence: Long?,
    val type: String,
    val appId: String,
    val roomUuid: String?,
    val version: Int,
    val data: Object,
)
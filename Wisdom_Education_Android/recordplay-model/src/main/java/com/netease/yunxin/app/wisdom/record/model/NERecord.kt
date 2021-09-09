/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.model

data class NERecord(
    val appId: String,
    val classBeginTimestamp: Long,
    val classEndTimestamp: Long,
    val recordId: String,
    val roomCid: String,
    val roomUuid: String,
    val startTime: Long,
    val stopTime: Long,
)
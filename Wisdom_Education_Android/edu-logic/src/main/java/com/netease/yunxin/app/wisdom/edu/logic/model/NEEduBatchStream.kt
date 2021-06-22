/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

data class NEEduBatchStream(
    val method: String,
    val operation: String,
    val operationId: String,
    val userUuid: String,
    val key: String,
    val value: NEEduState?,
)

object NEEduBatchParamKey {
    const val OPERATIONS = "operations"
    const val MEMBER_STREAMS = "member.streams"
}

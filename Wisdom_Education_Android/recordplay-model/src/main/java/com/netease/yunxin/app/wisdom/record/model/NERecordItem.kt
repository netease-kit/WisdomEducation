/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.model

import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoleType

data class NERecordItem(
    val duration: Int,
    val filename: String,
    val md5: String,
    val mix: Int,
    val pieceIndex: Int,
    val recordId: String,
    val role: String,
    val roomCid: String,
    val roomUid: Long,
    val roomUuid: String,
    val size: Int,
    val timestamp: Long,
    val type: String,
    val url: String,
    val userName: String,
    val subStream: Boolean,
    var offset: Long // The offset between the start time of the video and the benchmark
) {
    companion object {
        const val TYPE_WHITEBOARD = "gz"
        const val TYPE_VIDEO = "mp4"
        const val TYPE_AUDIO = "aac"
    }

    fun isHost(): Boolean {
        return role == NEEduRoleType.HOST.value
    }

    override fun toString(): String {
        return "NERecordItem(role='$role', roomUid=$roomUid, timestamp=$timestamp, type='$type', url='$url', userName='$userName', subStream=$subStream, offset=$offset)"
    }

}
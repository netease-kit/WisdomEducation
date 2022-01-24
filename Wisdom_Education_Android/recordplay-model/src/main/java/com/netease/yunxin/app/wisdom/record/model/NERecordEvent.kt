/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.model

/**
 * Event
 *
 * @property roomUid
 * @property timestamp
 * @property type
 * @property data custom data
 */
data class NERecordEvent(
    val roomUid: String,
    val timestamp: Long,
    @NERecordEventType
    val type: Int,
    val data: Object,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NERecordEvent

        if (roomUid != other.roomUid) return false
        if (type != other.type) return false
        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        var result = roomUid.hashCode()
        result = 31 * result + type
        result = 31 * result + data.hashCode()
        return result
    }
}
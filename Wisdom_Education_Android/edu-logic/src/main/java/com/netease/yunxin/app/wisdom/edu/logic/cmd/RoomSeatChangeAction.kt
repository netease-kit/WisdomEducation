/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.cmd

import com.google.gson.annotations.SerializedName

class RoomSeatChangeAction (
    appKey: String,
    roomUuid: String,
    val cmd: Int,
    val data: SeatItemChangeDetail,
): CMDAction(appKey, roomUuid)

 data class SeatItemChangeDetail(
    @SerializedName("roomUuid") val uuid: String,
    val seatList: List<SeatItemDetail>
)

data class SeatItemDetail(
    val userUuid: String?,
    @SerializedName("seatIndex") private val _seatIndex: Int?,
    val status: Int,
    val userName: String?,
    val icon: String?,
    val updated: Long
) {
    val seatIndex: Int
        get() = _seatIndex ?: -1
}
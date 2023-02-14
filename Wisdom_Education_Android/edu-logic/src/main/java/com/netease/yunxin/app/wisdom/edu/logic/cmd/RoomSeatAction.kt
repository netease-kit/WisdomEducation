package com.netease.yunxin.app.wisdom.edu.logic.cmd

import com.google.gson.annotations.SerializedName

private const val SEAT_NO_INDEX = -1

class RoomSeatAction(
      appKey: String,
      roomUuid: String,
     val cmd: Int,
    val data: SeatActionDetail): CMDAction(appKey, roomUuid)

data class SeatActionDetail(
    val seatUser: SeatUserDetail,
    val operatorUser: SeatOperatorUser
)

data class SeatUserDetail(
    @SerializedName("roomUuid") val uuid: String,
    val userUuid: String,
    @SerializedName("seatIndex") private val _seatIndex: Int?
) {
    val seatIndex: Int
        get() = _seatIndex ?: SEAT_NO_INDEX
}

data class SeatOperatorUser(
    val userUuid: String
)
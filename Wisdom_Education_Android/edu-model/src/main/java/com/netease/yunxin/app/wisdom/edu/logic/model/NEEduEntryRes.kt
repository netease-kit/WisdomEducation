/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

import android.os.Parcel
import android.os.Parcelable


data class NEEduEntryRes(
    val room: NEEduRoom,
    val member: NEEduEntryMember
) {
    fun isHost(): Boolean {
        return member.isHost()
    }
}

class NEEduEntryMember(
    val rtcKey: String,
    val rtcToken: String,
    role: String,
    userName: String,
    userUuid: String,
    rtcUid: Long,
    time: Long,
    streams: NEEduStreams,
    properties: NEEduMemberProperties?,
    val wbAuth: NEEduWbAuth?
) : NEEduMember(role, userName, userUuid, rtcUid, time, streams, properties)


data class NEEduWbAuth(
    val checksum: String?,
    val curTime: String?,
    val nonce: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(checksum)
        parcel.writeString(curTime)
        parcel.writeString(nonce)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NEEduWbAuth> {
        override fun createFromParcel(parcel: Parcel): NEEduWbAuth {
            return NEEduWbAuth(parcel)
        }

        override fun newArray(size: Int): Array<NEEduWbAuth?> {
            return arrayOfNulls(size)
        }
    }
}

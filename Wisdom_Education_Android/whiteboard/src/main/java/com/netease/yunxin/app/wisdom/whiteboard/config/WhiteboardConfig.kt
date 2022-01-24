/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.whiteboard.config

import android.os.Parcel
import android.os.Parcelable
import com.netease.yunxin.app.wisdom.whiteboard.model.NEWbAuth

/**
 * 
 */
class WhiteboardConfig(
    val appKey: String,
    val rtcUid: Long,
    val wbAuth: NEWbAuth?,
    val imAccid: String,
    val imToken: String,
    val channelName: String,
    var whiteBoardUrl: String?,
    val isHost: Boolean,
    val privateConf: NEWbPrivateConf?,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readParcelable(NEWbAuth.javaClass.classLoader),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readParcelable(NEWbPrivateConf.javaClass.classLoader),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(appKey)
        parcel.writeLong(rtcUid)
        parcel.writeParcelable(wbAuth, flags)
        parcel.writeString(imAccid)
        parcel.writeString(imToken)
        parcel.writeString(channelName)
        parcel.writeString(whiteBoardUrl)
        parcel.writeByte(if (isHost) 1 else 0)
        parcel.writeParcelable(privateConf, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WhiteboardConfig> {
        override fun createFromParcel(parcel: Parcel): WhiteboardConfig {
            return WhiteboardConfig(parcel)
        }

        override fun newArray(size: Int): Array<WhiteboardConfig?> {
            return arrayOfNulls(size)
        }
    }
}
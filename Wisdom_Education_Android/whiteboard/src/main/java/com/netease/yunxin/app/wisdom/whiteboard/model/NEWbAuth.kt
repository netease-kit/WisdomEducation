/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.whiteboard.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by hzsunyj on 2021/9/1.
 */
data class NEWbAuth(
    val checksum: String?,
    val curTime: String?,
    val nonce: String?,
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

    companion object CREATOR : Parcelable.Creator<NEWbAuth> {
        override fun createFromParcel(parcel: Parcel): NEWbAuth {
            return NEWbAuth(parcel)
        }

        override fun newArray(size: Int): Array<NEWbAuth?> {
            return arrayOfNulls(size)
        }
    }
}

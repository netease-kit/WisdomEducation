package com.netease.yunxin.app.wisdom.whiteboard.config

import android.os.Parcel
import android.os.Parcelable

/**
 * 白板私有化
 *
 */
data class NEWbPrivateConf(
    var roomServerAddr: String? = null,
    var sdkLogNosAddr: String? = null,
    var dataReportAddr: String? = null,
    var directNosAddr: String? = null,
    var mediaUploadAddr: String? = null,
    var docTransAddr: String? = null,
    var fontDownloadUrl: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(roomServerAddr)
        parcel.writeString(sdkLogNosAddr)
        parcel.writeString(dataReportAddr)
        parcel.writeString(directNosAddr)
        parcel.writeString(mediaUploadAddr)
        parcel.writeString(docTransAddr)
        parcel.writeString(fontDownloadUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NEWbPrivateConf> {
        override fun createFromParcel(parcel: Parcel): NEWbPrivateConf {
            return NEWbPrivateConf(parcel)
        }

        override fun newArray(size: Int): Array<NEWbPrivateConf?> {
            return arrayOfNulls(size)
        }
    }

}
/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.net.service

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.base.network.RetrofitManager
import com.netease.yunxin.app.wisdom.record.model.NEEduRecordData

/**
 * 
 */
object RecordPlayRepository {
    private val recordPlayService by lazy { getService(RecordPlayService::class.java) }
    lateinit var appKey: String
    lateinit var baseUrl: String

    fun recordPlayback(
        roomUuid: String,
        rtcCid: String,
    ): LiveData<NEResult<NEEduRecordData>> {
        return recordPlayService.recordPlayback(appKey, roomUuid, rtcCid)
    }

    private fun <T> getService(zClass: Class<T>): T {
        require(zClass.isInterface) { "API declarations must be interfaces." }
        return RetrofitManager.instance().getService(baseUrl, zClass)
    }
}
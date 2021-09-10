/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.net.service

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.record.model.NEEduRecordData
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Created by hzsunyj on 2021/8/27.
 */

internal interface RecordPlayService {
    /**
     * 录制回放
     */
    @GET("scene/apps/{appKey}/v1/rooms/{roomUuid}/{rtcCid}/record/playback")
    fun recordPlayback(
        @Path("appKey") appKey: String,
        @Path("roomUuid") roomUuid: String,
        @Path("rtcCid") rtcCid: String,
    ): LiveData<NEResult<NEEduRecordData>>
}
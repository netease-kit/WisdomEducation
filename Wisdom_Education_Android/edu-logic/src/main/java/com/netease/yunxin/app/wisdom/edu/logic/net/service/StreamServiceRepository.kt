/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduState
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.BatchReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.CommonReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.BatchStreamRes

object StreamServiceRepository : BaseRepository() {

    private val streamService = getDelegateService(StreamService::class.java)

    fun updateStreamInfo(
        roomUuid: String,
        userUuid: String,
        streamType: String,
        streamStatusReq: CommonReq,
    ): LiveData<NEResult<NEEduState>> {
        return streamService.updateStreamInfo(appId, roomUuid, userUuid, streamType, streamStatusReq)
    }

    fun deleteStream(
        roomUuid: String,
        userUuid: String,
        streamType: String,
    ): LiveData<NEResult<Void>> {
        return streamService.deleteStream(appId, roomUuid, userUuid, streamType)
    }

    fun batchStreams(roomUuid: String, req: BatchReq): LiveData<NEResult<BatchStreamRes>> {
        return streamService.batchStreams(appId, roomUuid, req)
    }
}
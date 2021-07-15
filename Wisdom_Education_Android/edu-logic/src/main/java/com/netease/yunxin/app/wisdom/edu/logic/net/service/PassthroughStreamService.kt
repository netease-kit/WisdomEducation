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
import java.lang.reflect.Method

/**
 * Created by hzsunyj on 2021/5/26.
 */
object PassthroughStreamService : StreamService, BaseService {

    private val streamService = getService(StreamService::class.java)

    override fun updateStreamInfo(
        appKey: String,
        roomUuid: String,
        userUuid: String,
        streamType: String,
        streamStatusReq: CommonReq,
    ): LiveData<NEResult<NEEduState>> {
        val method: Method? = findMethod(BaseService.UPDATE_STREAM_INFO)
        return if (overPassthrough() && method != null) {
            executeOverPassthrough(method, appKey, roomUuid, userUuid, streamType, streamStatusReq)
        } else {
            streamService.updateStreamInfo(appKey, roomUuid, userUuid, streamType, streamStatusReq)
        }
    }

    override fun deleteStream(
        appKey: String,
        roomUuid: String,
        userUuid: String,
        streamType: String,
    ): LiveData<NEResult<Void>> {
        val method: Method? = findMethod(BaseService.DELETE_STREAM)
        return if (overPassthrough() && method != null) {
            executeOverPassthrough(method, appKey, roomUuid, userUuid, streamType)
        } else {
            return streamService.deleteStream(appKey, roomUuid, userUuid, streamType)
        }
    }

    override fun batchStreams(appKey: String, roomUuid: String, req: BatchReq): LiveData<NEResult<BatchStreamRes>> {
        val method: Method? = findMethod(BaseService.BATCH_STREAMS)
        return if (overPassthrough() && method != null) {
            executeOverPassthrough(method, appKey, roomUuid, req)
        } else {
            return streamService.batchStreams(appKey, roomUuid, req)
        }
    }
}
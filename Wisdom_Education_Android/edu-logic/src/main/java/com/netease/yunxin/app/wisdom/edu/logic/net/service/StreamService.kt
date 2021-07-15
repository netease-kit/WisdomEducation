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
import retrofit2.http.*

internal interface StreamService{

    /**
     * 更新流状态
     */
    @PUT("/scene/apps/{appKey}/v1/rooms/{roomUuid}/members/{userUuid}/streams/{streamType}")
    fun updateStreamInfo(
        @Path("appKey") appKey: String,
        @Path("roomUuid") roomUuid: String,
        @Path("userUuid") userUuid: String,
        @Path("streamType") streamType: String,
        @Body eduStreamStatusReq: CommonReq,
    ): LiveData<NEResult<NEEduState>>

    /**
     * 删除流
     */
    @DELETE("/scene/apps/{appKey}/v1/rooms/{roomUuid}/members/{userUuid}/streams/{streamType}")
    fun deleteStream(
        @Path("appKey") appKey: String,
        @Path("roomUuid") roomUuid: String,
        @Path("userUuid") userUuid: String,
        @Path("streamType") streamType: String,
    ): LiveData<NEResult<Void>>

    /**
     * 删除流
     */
    @POST("/scene/apps/{appKey}/v1/rooms/{roomUuid}/batch")
    fun batchStreams(
        @Path("appKey") appKey: String,
        @Path("roomUuid") roomUuid: String,
        @Body req: BatchReq,
    ): LiveData<NEResult<BatchStreamRes>>

}
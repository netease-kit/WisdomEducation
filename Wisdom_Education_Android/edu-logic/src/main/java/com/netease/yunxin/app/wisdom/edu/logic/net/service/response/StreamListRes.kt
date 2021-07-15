/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service.response

internal class StreamListRes(
        var count: Int,
        var total: Int,
        var nextId: String,
        var list: MutableList<StreamRes>
)

open class StreamRes(
        var fromUser: FromUserRes,
        streamUuid: String,
        streamName: String,
        videoSourceType: Int,
        audioSourceType: Int,
        videoState: Int,
        audioState: Int,
        updateTime: Long,
        state: Int?) : BaseStreamRes(streamUuid, streamName, videoSourceType, audioSourceType, videoState,
        audioState, updateTime, state)

open class BaseStreamRes(
        var streamUuid: String,
        var streamName: String,
        var videoSourceType: Int,
        var audioSourceType: Int,
        var videoState: Int,
        var audioState: Int,
        var updateTime: Long,
        var state: Int?
)

class FromUserRes(
        var userUuid: String,
        var userName: String,
        var role: String
)
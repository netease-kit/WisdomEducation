/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

/**
 * Created by hzsunyj on 2021/5/20.
 */
class NEEduStreams(
    var audio: NEEduStreamAudio?,
    var video: NEEduStreamVideo?,
    var subVideo: NEEduStreamSubVideo? = null,
) {
    /**
     * 同步流状态
     */
    fun merge(targetStreams: NEEduStreams): NEEduStreams {
        targetStreams.audio?.let {
            this.audio = it
        }
        targetStreams.video?.let {
            this.video = it
        }
        targetStreams.subVideo?.let {
            this.subVideo = it
        }
        return this
    }

    fun reset() {
        audio = null
        video = null
        subVideo = null
    }

    fun delete(streamType: String): NEEduStreams? {
        when (streamType) {
            NEEduStreamType.AUDIO.type -> audio = null
            NEEduStreamType.VIDEO.type -> video = null
            NEEduStreamType.SUB_VIDEO.type -> subVideo = null
        }
        return this
    }
}

class NEEduStreamAudio(value: Int = NEEduStateValue.OPEN, time: Long? = null) : NEEduState(value, time)

class NEEduStreamVideo(value: Int = NEEduStateValue.OPEN, time: Long? = null) : NEEduState(value, time)

class NEEduStreamSubVideo(value: Int = NEEduStateValue.OPEN, time: Long? = null) : NEEduState(value, time)

enum class NEEduStreamType(val type: String) {
    AUDIO("audio"),
    VIDEO("video"),
    SUB_VIDEO("subVideo"),
}
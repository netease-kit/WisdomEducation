/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

data class NEEduMemberProperties(
    var screenShare: NEEduScreenShareProperty?,
    var whiteboard: NEEduWhiteboardProperty?,
    var streamAV: NEEduStreamAVProperty?,
    var avHandsUp: NEEduAvHandsUpProperty?,
) {
    fun merge(properties: NEEduMemberProperties): NEEduMemberProperties {
        if (properties.screenShare != null) {
            screenShare = properties.screenShare
        }
        if (properties.whiteboard != null) {
            whiteboard = properties.whiteboard
        }
        if (properties.streamAV != null) {
            streamAV = properties.streamAV
        }
        if (properties.avHandsUp != null) {
            avHandsUp = properties.avHandsUp
        }
        return this
    }

    /**
     * Compare the calculated difference
     */
    fun diff(properties: NEEduMemberProperties?): NEEduMemberProperties {
        if(properties == null) {
            return this
        }
        var result = NEEduMemberProperties(null, null, null, null)
        if (properties.screenShare != screenShare) {
            result.screenShare = screenShare
        }
        if (properties.whiteboard != whiteboard) {
            result.whiteboard = whiteboard
        }
        if (properties.streamAV != streamAV) {
            result.streamAV = streamAV
        }
        if (properties.avHandsUp != avHandsUp) {
            result.avHandsUp = avHandsUp
        }
        return result
    }

    fun isHandsUp(): Boolean {
        return avHandsUp?.value == NEEduHandsUpStateValue.APPLY
    }

    fun isHandsUpReject(): Boolean {
        return avHandsUp?.value == NEEduHandsUpStateValue.TEACHER_REJECT
    }

    fun isOffStage(): Boolean {
        return avHandsUp?.value == NEEduHandsUpStateValue.TEACHER_OFF_STAGE
    }

    fun isOnStage(): Boolean {
        return avHandsUp?.value == NEEduHandsUpStateValue.TEACHER_ACCEPT
    }
}

class NEEduAvHandsUpProperty(value: Int, time: Long) : NEEduState(value, time)

class NEEduScreenShareProperty(value: Int, time: Long) : NEEduState(value, time)

class NEEduWhiteboardProperty(drawable: Int, time: Long) : NEEduDrawableState(drawable, time)

class NEEduStreamAVProperty(
    value: Int?, time: Long, val audio: Int?,
    val video: Int?,
) : NEEduState(value, time)



/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

/**
 * Created by hzsunyj on 2021/5/18.
 */
class NEEduRoom(
    val roomName: String,
    val roomUuid: String,
    val rtcCid: String,
    var states: NEEduRoomStates?,
    var properties: MutableMap<String, Any>?,
) {
    private fun chatRoom(): MutableMap<String, Any> {
        return properties?.get("chatRoom") as MutableMap<String, Any>
    }

    private fun whiteBoard(): MutableMap<String, Any> {
        return properties?.get("whiteboard") as MutableMap<String, Any>
    }


    fun chatRoomId(): String {
        return (chatRoom()["chatRoomId"] as Double).toLong().toString()
    }

    fun whiteBoardCName(): String {
        return whiteBoard()["channelName"] as String
    }
}

class NEEduRoomStates(
    /**开始&结束课堂*/
    var step: NEEduState?,
    /**暂停&继续课堂*/
    var pause: NEEduState?,
    /**聊天室全体禁言*/
    var muteChat: NEEduState?,
    /**全体禁止视频*/
    var muteVideo: NEEduState?,
    /**全体静音*/
    var muteAudio: NEEduState?,
) {
    companion object {
        const val STATE_STEP = "step"
        const val STATE_PAUSE = "pause"
        const val STATE_MUTEAUDIO = "muteAudio"
        const val STATE_MUTECHAT = "muteChat"
        const val STATE_MUTEVIDEO = "muteVideo"
    }

    fun updateDuration(ts: Long) {
        if (step?.value == NEEduRoomStep.START.value) {
            duration = if (ts < step!!.time!!) 0 else ts - step!!.time!!
        }
    }

    var duration: Long? = null

    fun merge(states: NEEduRoomStates) {
        states?.duration.let {
            duration = it
        }
        if (states.step != null) {
            step = states.step
        }
        if (states.pause != null) {
            pause = states.pause
        }
        if (states.muteChat != null) {
            muteChat = states.muteChat
        }
        if (states.muteVideo != null) {
            muteVideo = states.muteVideo
        }
        if (states.muteAudio != null) {
            muteAudio = states.muteAudio
        }
    }
}
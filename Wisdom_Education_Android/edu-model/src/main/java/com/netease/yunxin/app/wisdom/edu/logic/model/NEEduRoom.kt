/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

/**
 * 
 */
class NEEduRoom(
    val roomName: String,
    val roomUuid: String,
    val rtcCid: String,
    var states: NEEduRoomStates?,
    var properties: MutableMap<String, Any>?,
) {
    private fun chatRoom(): MutableMap<String, Any> {
        properties?.get("chatRoom")?.apply {
            @Suppress("UNCHECKED_CAST")
            return this as MutableMap<String, Any>
        }
        return mutableMapOf()
    }

    private fun whiteBoard(): MutableMap<String, Any> {
        properties?.get("whiteboard")?.apply {
            @Suppress("UNCHECKED_CAST")
            return this as MutableMap<String, Any>
        }
        return mutableMapOf()
    }

    private fun live(): MutableMap<String, Any> {
        properties?.get("live")?.apply {
            @Suppress("UNCHECKED_CAST")
            return this as MutableMap<String, Any>
        }
        return mutableMapOf()
    }

    fun chatRoomId(): String? {
        chatRoom()["chatRoomId"]?.apply {
            @Suppress("UNCHECKED_CAST")
            return (this as Double).toLong().toString()
        }
        return null
    }

    fun whiteBoardCName(): String? {
        return whiteBoard()["channelName"]?.toString()
    }

    fun pullRtmpUrl(): String? {
        return live()["pullRtmpUrl"]?.toString()
    }

    fun pullRtsUrl(): String? {
        return live()["pullRtsUrl"]?.toString()
    }

    fun pushUrl(): String? {
        return live()["pushUrl"]?.toString()
    }
}

class NEEduRoomStates(
    /**Start&end class*/
    var step: NEEduState?,
    /**Pause/resume class*/
    var pause: NEEduState?,
    /**Mute all chat*/
    var muteChat: NEEduState?,
    /**Mute all video*/
    var muteVideo: NEEduState?,
    /**Mute all audio*/
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
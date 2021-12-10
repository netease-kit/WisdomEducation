/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

import android.text.TextUtils

/**
 * Created by hzsunyj on 2021/5/17.
 */
open class NEEduMember(
    var role: String,
    var userName: String,
    val userUuid: String,
    val rtcUid: Long,
    val time: Long,
    var streams: NEEduStreams?,
    var properties: NEEduMemberProperties?,
) {

    companion object {

        private const val HOLDER_ID: String = "-123"

        fun buildHoldTeacherMember(): NEEduMember {
            return buildHoldMember(NEEduRoleType.HOST)
        }

        fun buildHoldStudentMember(): NEEduMember {
            return buildHoldMember(NEEduRoleType.BROADCASTER)
        }

        private fun buildHoldMember(type: NEEduRoleType): NEEduMember {
            return NEEduMember(type.value, "", HOLDER_ID, 0L, 0, null, null)
        }

        fun buildLoadMoreHoldMember(type: NEEduRoleType): NEEduMember {
            return NEEduMember(type.value, "", HOLDER_ID, 0L, -1, null, null)
        }
    }
    override fun hashCode(): Int {
        val var10000 = userUuid
        var var1 = var10000.hashCode() * 31
        val var10001 = userName
        var1 = (var1 + var10001.hashCode()) * 31
        val var2 = role
        var1 = (var1 + var2.hashCode()) * 31
        return var1
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is NEEduMember) {
            return false
        }
        return (other.userUuid == this.userUuid && other.userName == this.userName && other.role == this.role &&
                other.rtcUid == this.rtcUid)
    }

    fun isHolder(): Boolean {
        return TextUtils.equals(userUuid, HOLDER_ID)
    }

    fun isHost(): Boolean {
        return role == NEEduRoleType.HOST.value
    }

    fun isHandsUp(): Boolean {
        return properties?.isHandsUp() ?: false
    }

    fun isHandsUpReject(): Boolean {
        return properties?.isHandsUpReject() ?: false
    }

    fun isOffStage(): Boolean {
        return properties?.isOffStage() ?: false
    }

    fun isOnStage(): Boolean {
        return properties?.isOnStage() ?: false
    }

    fun hasVideo(): Boolean {
        return streams?.video != null
    }

    fun hasSubVideo(): Boolean {
        return streams?.subVideo != null
    }

    fun hasAudio(): Boolean {
        return streams?.audio != null
    }

    fun isGrantedScreenShare(): Boolean {
        return properties?.screenShare?.value == NEEduStateValue.OPEN
    }

    fun isGrantedWhiteboard(): Boolean {
        return properties?.whiteboard?.drawable == NEEduStateValue.OPEN
    }

    fun updateVideo(state: NEEduStreamVideo?) {
        streams?.video = state
    }

    fun updateAudio(state: NEEduStreamAudio?) {
        streams?.audio = state
    }

    fun updateSubVideo(state: NEEduStreamSubVideo?) {
        streams?.subVideo = state
    }

    override fun toString(): String {
        return "NEEduMember(role='$role', userName='$userName', userUuid='$userUuid', rtcUid=$rtcUid, time=$time)"
    }
}
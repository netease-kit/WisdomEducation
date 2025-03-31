/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.cmd

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduCMDBody
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduIMCMDMessage
import com.netease.yunxin.kit.alog.ALog

/**
 * 
 */
object CMDActionFactory {

    private const val tag: String = "CMDActionFactory"

    private val gson: Gson = Gson()

    private val map: MutableMap<Int, Class<*>> = mutableMapOf()

    init {
        map[CMDId.ROOM_STATES_CHANGE] = RoomStateChangeAction::class.java
        map[CMDId.ROOM_STATES_DELETE] = RoomStateRemoveAction::class.java
        map[CMDId.ROOM_PROPERTIES_CHANGE] = RoomPropertiesChangeAction::class.java
        map[CMDId.ROOM_PROPERTIES_DELETE] = RoomPropertiesRemoveAction::class.java
        map[CMDId.ROOM_MEMBER_PROPERTIES_CHANGE] = RoomMemberPropertiesChangeAction::class.java
        map[CMDId.ROOM_MEMBER_PROPERTIES_DELETE] = RoomMemberPropertiesRemoveAction::class.java
        map[CMDId.USER_JOIN] = RoomMemberJoinAction::class.java
        map[CMDId.USER_LEAVE] = RoomMemberLeaveAction::class.java
        map[CMDId.STREAM_CHANGE] = StreamChangeAction::class.java
        map[CMDId.STREAM_REMOVE] = StreamRemoveAction::class.java
        map[CMDId.SEAT_ITEM_CHANGE] = RoomSeatChangeAction::class.java
    }

    fun parse(text: String): NEEduCMDBody? {
        return try {
            gson.fromJson(text, object : TypeToken<NEEduCMDBody>() {}.type)
        } catch (e: Throwable) {
            ALog.i(tag, "get cmd action fail $text")
            null
        }
    }

    fun parseIMCMDMessage(text: String): NEEduIMCMDMessage? {
        return try {
            gson.fromJson(text, object : TypeToken<NEEduIMCMDMessage>() {}.type)
        } catch (e: Throwable) {
            ALog.i(tag, "get cmd action fail $text")
            null
        }
    }

    fun <T> getRealAction(cmdBody: NEEduCMDBody, zClass: Class<T>): T? {
        return try {
            gson.fromJson(gson.toJson(cmdBody.data), zClass)
        } catch (e: Throwable) {
            ALog.i(tag, "get real action fail ${cmdBody.data}, zClass=${zClass}")
            null
        }
    }

    fun <T> getRealAction(cmdBody: NEEduCMDBody): T? {
        val cmd = cmdBody.cmd
        var zClass: Class<*>? = map[cmdBody.cmd]
        if ((cmd == CMDId.SEAT_APPROVE_REQUEST) || (cmd == CMDId.SEAT_SUBMIT_REQUEST)
            || (cmd == CMDId.SEAT_CANCEL_REQUEST) || (cmd == CMDId.SEAT_LEAVE)
            || (cmd == CMDId.SEAT_REJECT_REQUEST)  || (cmd == CMDId.SEAT_KICK)) {
            zClass =  RoomSeatAction::class.java
            return zClass?.let {
                try {
                    gson.fromJson(gson.toJson(cmdBody), zClass) as T
                } catch (e: Throwable) {
                    ALog.i(tag, "get real action fail ${cmdBody.data}, zClass=${zClass}")
                    null
                }
            }
        }
        if(cmd == CMDId.SEAT_ITEM_CHANGE){
            return zClass?.let {
                try {
                    gson.fromJson(gson.toJson(cmdBody), zClass) as T
                } catch (e: Throwable) {
                    ALog.i(tag, "get real action fail ${cmdBody.data}, zClass=${zClass}")
                    null
                }
            }
        }
        return zClass?.let {
            try {
                gson.fromJson(gson.toJson(cmdBody.data), zClass) as T
            } catch (e: Throwable) {
                ALog.i(tag, "get real action fail ${cmdBody.data}, zClass=${zClass}")
                null
            }
        }
    }
}
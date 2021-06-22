/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.cmd

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduCMDBody
import com.netease.yunxin.kit.alog.ALog

/**
 * Created by hzsunyj on 2021/5/17.
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
    }

    fun parse(text: String): NEEduCMDBody? {
        return try {
            gson.fromJson(text, object : TypeToken<NEEduCMDBody>() {}.type)
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
        val zClass: Class<*>? = map[cmdBody.cmd]
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
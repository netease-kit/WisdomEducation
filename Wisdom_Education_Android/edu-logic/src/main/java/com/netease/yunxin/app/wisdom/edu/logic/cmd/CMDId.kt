/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.cmd

/**
 * Created by hzsunyj on 2021/5/17.
 */
object CMDId {
    // 房间状态group
    /**
     *房间状态变更
     */
    const val ROOM_STATES_CHANGE = 1

    /**
     *房间状态删除
     */
    const val ROOM_STATES_DELETE = 2

    // 房间属性group
    /**
     *房间属性变更
     */
    const val ROOM_PROPERTIES_CHANGE = 10

    /**
     *房间属性删除:
     */
    const val ROOM_PROPERTIES_DELETE = 11


    // 房间成员属性group
    /**
     *房间成员属性变更
     */
    const val ROOM_MEMBER_PROPERTIES_CHANGE = 20

    /**
     *房间成员属性删除
     */
    const val ROOM_MEMBER_PROPERTIES_DELETE = 21

    // 用户进入离开group
    /**
     *用户加入
     */
    const val USER_JOIN = 30

    /**
     *用户离开
     */
    const val USER_LEAVE = 31

    // 房间成员流group
    /**
     *房间成员流变更
     */
    const val STREAM_CHANGE = 40

    /**
     *房间成员流移除
     */
    const val STREAM_REMOVE = 41

}
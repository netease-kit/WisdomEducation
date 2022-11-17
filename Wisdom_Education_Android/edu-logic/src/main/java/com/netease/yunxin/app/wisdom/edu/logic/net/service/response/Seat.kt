/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service.response

data class NESeatInfo(
    val seatIndexList: List<NESeatItem>,
    val seatCreatorUserUuid: String,
    val seatManagerUserUuidList: List<String>?
)
 data class NESeatItem(
    val seatIndex: Int,
    val userUuid: String?,
    val userName: String?,
    val icon: String?,
    val status: Int,
    val updated: Long
)

data class NESeatRequestItem(
    val index: Int,
    val userUuid: String,
    val userName: String?,
    val icon: String?
)

/**
 * 麦位状态
 */
object NESeatItemStatus {

    /**
     * 麦位初始化（无人，可以上麦）
     */
    const val INITIAL = 0

    /**
     * 该麦位正在等待管理员通过申请或等待成员接受邀请后上麦。
     */
    const val WAITING = 1

    /**
     * 当前麦位已被占用
     */
    const val TAKEN = 2

    /**
     * 当前麦位已关闭，不能操作上麦
     */
    const val CLOSED = -1
}

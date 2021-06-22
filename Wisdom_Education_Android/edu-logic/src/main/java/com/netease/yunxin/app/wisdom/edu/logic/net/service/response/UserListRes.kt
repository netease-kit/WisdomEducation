/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service.response

data class UserListRes(
    var count: Int,
    var total: Int,
    var nextId: String,
    var list: MutableList<UserRes>,
)

open class UserRes(
    var userUuid: String,
    var userName: String,
    var role: String,
    var muteChat: Int,
    var updateTime: Long?,
    var state: Int?,
) {
}
/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.cmd

import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember

/**
 * 
 */
class RoomPropertiesRemoveAction(
    appKey: String,
    roomUuid: String,
    val key: String,
    val operatorMember: NEEduMember,
) : CMDAction(appKey, roomUuid)
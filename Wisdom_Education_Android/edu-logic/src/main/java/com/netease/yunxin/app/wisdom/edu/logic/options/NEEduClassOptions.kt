/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.options

import com.netease.yunxin.app.wisdom.edu.logic.model.*

/**
 * Class configuration
 *
 * @property classId Class ID
 * @property className Class name
 * @property nickName User alias in the class
 * @property sceneType Class type: One-to-one, breakout class, and large class
 * @property roleType Role type: host. In the education scenario, the host represents the teacher，and broadcaster indicates students
 * @property isRtcRoom room is rtc
 */
class NEEduClassOptions(
    var classId: String,
    var className: String,
    var nickName: String,
    var sceneType: NEEduSceneType,
    var roleType: NEEduRoleType,
    val config: NEEduConfig,
    var isRtcRoom:Boolean = false,
)
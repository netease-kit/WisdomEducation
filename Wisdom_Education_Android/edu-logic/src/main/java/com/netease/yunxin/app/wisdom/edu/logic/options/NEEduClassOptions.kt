/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.options

import com.netease.yunxin.app.wisdom.edu.logic.model.*

/**
 * Created by hzsunyj on 4/21/21.
 */
class NEEduClassOptions(
    private var classId: String,
    var className: String,
    var nickName: String,
    var sceneType: NEEduSceneType,
    var roleType: NEEduRoleType,
) {

    fun classId(): String {
        return "$classId${sceneType.configId()}"
    }
}

/**
 * 场景值，教育目前三类： 1v1， 小班课， 大班课
 */
enum class NEEduSceneType(var value: String) {
    ONE_TO_ONE("EDU.1V1"),
    SMALL("EDU.SMALL"),
    BIG("EDU.BIG");

    fun configId(): Int {
        return when (this) {
            ONE_TO_ONE -> 5
            SMALL -> 6
            BIG -> 7
        }
    }

    fun streams(roleType: NEEduRoleType): NEEduStreams? {
        return when (this) {
            ONE_TO_ONE -> NEEduStreams(
                audio = NEEduStreamAudio(),
                video = NEEduStreamVideo())
            SMALL -> NEEduStreams(
                audio = NEEduStreamAudio(),
                video = NEEduStreamVideo())
            BIG -> if (roleType == NEEduRoleType.AUDIENCE) null else NEEduStreams(audio = NEEduStreamAudio(), video = NEEduStreamVideo())
        }
    }
}

/**
 * 角色类型： host： 教育场景中映射为老师， broadcaster: 教育场景中映射为学生， audience： 教育场景中映射为观众，主要在大班课中使用
 */
enum class NEEduRoleType(var value: String) {
    HOST("host"),
    BROADCASTER("broadcaster"),
    AUDIENCE("audience")
}
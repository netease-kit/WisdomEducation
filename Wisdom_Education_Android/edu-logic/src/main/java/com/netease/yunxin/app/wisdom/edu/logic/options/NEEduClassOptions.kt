/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.options

import com.netease.yunxin.app.wisdom.edu.logic.model.*

/**
 * 课堂配置
 *
 * @property classId 课程号
 * @property className 课程名称
 * @property nickName 用户在课堂中的昵称
 * @property sceneType 课堂类型，有三种类型： 1v1， 小班课， 大班课
 * @property roleType 角色类型：host：教育场景中映射为老师，broadcaster: 教育场景中映射为学生
 */
class NEEduClassOptions(
    var classId: String,
    var className: String,
    var nickName: String,
    var sceneType: NEEduSceneType,
    var roleType: NEEduRoleType,
    val config: NEEduConfig
)
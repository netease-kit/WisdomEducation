/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

/**
 * 角色类型： host： 教育场景中映射为老师， broadcaster: 教育场景中映射为学生， audience： 教育场景中映射为观众，主要在大班课中使用
 */
enum class NEEduRoleType(var value: String) {
    HOST("host"),
    BROADCASTER("broadcaster"),
    AUDIENCE("audience")
}
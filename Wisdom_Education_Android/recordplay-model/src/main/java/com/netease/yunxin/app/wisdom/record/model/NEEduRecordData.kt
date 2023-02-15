/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.model

import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduSceneType

class NEEduRecordData(
    val sceneType: String,
    eventList: MutableList<NERecordEvent>,
    record: NERecord,
    recordItemList: MutableList<NERecordItem>,
    val snapshotDto: NESnapshotDto
) : NERecordData(eventList, record, recordItemList) {
    fun is1V1(): Boolean {
        return sceneType == NEEduSceneType.ONE_TO_ONE.value
    }

    fun isBig(): Boolean {
        return sceneType == NEEduSceneType.BIG.value
    }

    fun isSmall(): Boolean {
        return sceneType == NEEduSceneType.SMALL.value
    }

    fun isLiveSimple(): Boolean {
        return sceneType == NEEduSceneType.LIVE_SIMPLE.value
    }
}
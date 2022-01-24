/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

/**
 * Scenario value: 1v1, breakout class, large class
 */
enum class NEEduSceneType(var value: String) {
    ONE_TO_ONE("EDU.1V1"),
    SMALL("EDU.SMALL"),
    BIG("EDU.BIG"),
    LIVE_SIMPLE("EDU.LIVE_SIMPLE");

    fun configId(): Int {
        return when (this) {
            ONE_TO_ONE -> 5
            SMALL -> 6
            BIG -> 7
            LIVE_SIMPLE -> 20
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
            else -> null
        }
    }
}
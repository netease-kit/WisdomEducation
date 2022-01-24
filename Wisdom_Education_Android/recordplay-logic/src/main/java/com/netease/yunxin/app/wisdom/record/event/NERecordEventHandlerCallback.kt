/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.record.event

import com.netease.yunxin.app.wisdom.record.model.NERecordItem

interface NERecordEventHandlerCallback {
    /**
     * The callback is triggered if a member joins or leaves the speaker group and joins or leaves the room. 
     *
     * @param inVideoList Raise hand accepted or joining the room
     * @param outVideoList Raise hand rejected or leave the room
     */
    fun onMemberVideoChange(inVideoList: MutableList<NERecordItem>, outVideoList: MutableList<NERecordItem>)
}
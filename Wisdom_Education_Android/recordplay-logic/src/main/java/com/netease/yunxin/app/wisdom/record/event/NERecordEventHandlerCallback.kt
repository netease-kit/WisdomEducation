/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.record.event

import com.netease.yunxin.app.wisdom.record.model.NERecordItem

interface NERecordEventHandlerCallback {
    /**
     * 上下台/进出房间 音视频UI回调
     *
     * @param inVideoList 上台/进入房间列表
     * @param outVideoList 下台/离开房间列表
     */
    fun onMemberVideoChange(inVideoList: MutableList<NERecordItem>, outVideoList: MutableList<NERecordItem>)
}
/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.impl

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.record.NERecordPlayUiKit
import com.netease.yunxin.app.wisdom.record.NERecordPlayer

/**
 * Created by hzsunyj on 2021/8/27.
 */
internal class NERecordPlayUiKitImpl : NERecordPlayUiKit {

    fun createPlayer(
        roomUuid: String,
        rtcCid: String,
    ): LiveData<NEResult<NERecordPlayer>> {
        return NERecordPlayer.createPlayer(roomUuid, rtcCid)
    }

}
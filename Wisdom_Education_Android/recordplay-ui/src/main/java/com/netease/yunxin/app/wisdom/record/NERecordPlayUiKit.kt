/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.record.impl.NERecordPlayUiKitImpl

/**
 * record player UIKit
 */
interface NERecordPlayUiKit {

    companion object {

        /**
         * Create the instance of a record player
         *
         * @param roomUuid
         * @param rtcCid
         * @return
         */
        fun createPlayer(
            roomUuid: String,
            rtcCid: String,
        ): LiveData<NEResult<NERecordPlayer>> {
            val playUiKit = NERecordPlayUiKitImpl()
            return playUiKit.createPlayer(roomUuid, rtcCid)
        }
    }
}
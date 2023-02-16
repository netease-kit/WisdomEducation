/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.ExecuteUserSeatActionReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.UserSeatAction
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NESeatInfo
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NESeatRequestItem

object SeatServiceRepository : BaseRepository() {

    private val seatService = getDelegateService(SeatService::class.java)

    fun submitSeatRequest(
        roomUuid: String,
        userName:String?
    ): LiveData<NEResult<Void>> {
        return interceptor(seatService.executeUserAction(appKey, roomUuid, ExecuteUserSeatActionReq(
            UserSeatAction.SUBMIT_REQUEST,userName)))
    }

    fun cancelSeatRequest(
        roomUuid: String,
        userName:String?
    ): LiveData<NEResult<Void>>{
        return interceptor(seatService.executeUserAction(appKey, roomUuid, ExecuteUserSeatActionReq(
            UserSeatAction.CANCEL_REQUEST,userName)))
    }

    fun leaveSeat(
        roomUuid: String,
        userName:String?
    ): LiveData<NEResult<Void>>{
        return interceptor(seatService.executeUserAction(appKey, roomUuid, ExecuteUserSeatActionReq(
            UserSeatAction.LEAVE,userName)))
    }

    fun getSeatInfo(roomUuid: String):LiveData<NEResult<NESeatInfo>>{
        return interceptor(seatService.getSeatInfo(appKey,roomUuid))
    }

    fun getSeatRequestList(roomUuid: String): LiveData<NEResult<List<NESeatRequestItem>>> {
        return interceptor(seatService.getSeatRequestList(appKey,roomUuid))
    }

}
package com.netease.yunxin.app.wisdom.edu.logic.net.service

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.ExecuteUserSeatActionReq
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NESeatInfo
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NESeatRequestItem

object PassthroughSeatService:SeatService,BaseService {

    private val seatService = getService(SeatService::class.java)

    override fun executeUserAction(
        appKey: String,
        uuid: String,
        eduStreamStatusReq: ExecuteUserSeatActionReq
    ): LiveData<NEResult<Void>> {
        return seatService.executeUserAction(appKey,uuid,eduStreamStatusReq)
    }

    override fun getSeatInfo(appKey: String, uuid: String): LiveData<NEResult<NESeatInfo>> {
        return seatService.getSeatInfo(appKey,uuid)
    }

    override fun getSeatRequestList(
        appKey: String,
        uuid: String
    ): LiveData<NEResult<List<NESeatRequestItem>>> {
       return seatService.getSeatRequestList(appKey,uuid)
    }

}
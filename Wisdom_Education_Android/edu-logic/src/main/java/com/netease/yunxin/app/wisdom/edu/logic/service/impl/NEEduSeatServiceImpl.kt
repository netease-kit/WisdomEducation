package com.netease.yunxin.app.wisdom.edu.logic.service.impl

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.net.service.SeatServiceRepository
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NESeatInfo
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NESeatRequestItem
import com.netease.yunxin.app.wisdom.edu.logic.service.NEEduSeatEventListener
import com.netease.yunxin.app.wisdom.edu.logic.service.NEEduSeatService

internal class NEEduSeatServiceImpl : NEEduSeatService() {

    private val listeners by lazy {
        ArrayList<NEEduSeatEventListener>()
    }

    override fun submitSeatRequest(roomUuid: String, userName: String?): LiveData<NEResult<Void>> {
        return SeatServiceRepository.submitSeatRequest(roomUuid, userName)
    }

    override fun cancelSeatRequest(roomUuid: String, userName: String?): LiveData<NEResult<Void>> {
        return SeatServiceRepository.cancelSeatRequest(roomUuid, userName)
    }

    override fun leaveSeat(roomUuid: String, userName: String?): LiveData<NEResult<Void>> {
        return SeatServiceRepository.leaveSeat(roomUuid, userName)
    }

    override fun getSeatInfo(roomUuid: String): LiveData<NEResult<NESeatInfo>> {
        return SeatServiceRepository.getSeatInfo(roomUuid)
    }

    override fun getSeatRequestList(roomUuid: String): LiveData<NEResult<List<NESeatRequestItem>>> {
        return SeatServiceRepository.getSeatRequestList(roomUuid)
    }

    override fun addSeatListener(listener: NEEduSeatEventListener) {
        listeners.add(listener)
    }

    override fun removeSeatListener(listener: NEEduSeatEventListener) {
        listeners.remove(listener)
    }

    override fun getListeners(): List<NEEduSeatEventListener> {
        return listeners
    }

    override fun dispose() {
        super.dispose()
        listeners.clear();
    }

}
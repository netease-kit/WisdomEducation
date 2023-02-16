/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.model.NESeatItem
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NESeatInfo
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NESeatRequestItem

/**
 * Methods for seat
 *
 */
abstract class NEEduSeatService : INEEduService() {

    abstract fun submitSeatRequest(roomUuid: String,userName:String?): LiveData<NEResult<Void>>

    abstract fun cancelSeatRequest(roomUuid: String,userName:String?): LiveData<NEResult<Void>>

    abstract fun leaveSeat(roomUuid: String,userName:String?): LiveData<NEResult<Void>>

    abstract fun getSeatInfo(roomUuid: String): LiveData<NEResult<NESeatInfo>>

    abstract fun getSeatRequestList(roomUuid:String):LiveData<NEResult<List<NESeatRequestItem>>>

    abstract fun addSeatListener(listener:NEEduSeatEventListener)

    abstract fun removeSeatListener(listener:NEEduSeatEventListener)

    internal abstract fun getListeners():List<NEEduSeatEventListener>
}

interface  NEEduSeatEventListener{
     fun onSeatRequestSubmitted(user:String)

     fun onSeatRequestCancelled(user:String)

     fun onSeatRequestApproved(user: String, operateBy: String)

     fun onSeatRequestRejected(user: String, operateBy: String)

     fun onSeatLeave(user: String)

     fun onSeatKicked(user: String, operateBy: String)

     fun onSeatListChanged(seats:List<NESeatItem>)
}
/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.impl.NEEduManagerImpl
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduAvHandsUpProperty
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduHandsUpStateValue
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.net.service.UserServiceRepository
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.NEEduMemberPropertiesType
import com.netease.yunxin.app.wisdom.edu.logic.net.service.request.NEEduUpdateMemberPropertyReq
import com.netease.yunxin.app.wisdom.edu.logic.service.NEEduHandsUpService

internal class NEEduHandsUpServiceImpl : NEEduHandsUpService() {
    private val handsUpStateLD: MediatorLiveData<List<NEEduMember>> = MediatorLiveData()
    private val handsUpStateList: MutableList<NEEduMember> = mutableListOf()

    override fun getHandsUpApplyList(): MutableList<NEEduMember> {
        return handsUpStateList.filter { it.isHandsUp() } as MutableList<NEEduMember>
    }

    override fun getOnStageMemberList(): MutableList<NEEduMember> {
        return handsUpStateList.filter { it.isOnStage() } as MutableList<NEEduMember>
    }

    /**
     * 更新台上成员列表
     *
     * @param list 更新成员
     * @param increment 是否增量更新
     */
    override fun updateMemberJoin(list: MutableList<NEEduMember>, increment: Boolean) {
        synchronized(this) {
            if (!increment) list.firstOrNull { NEEduManagerImpl.isSelf(it.userUuid) && !it.isHost() }?.properties?.let {
                if (it.avHandsUp == null) it.avHandsUp = NEEduAvHandsUpProperty(
                    NEEduHandsUpStateValue.IDLE, -1L
                )
            }
            val updateList = list.filter { it.properties?.avHandsUp != null }
            if (increment) {
                for (element in updateList) {
                    var index = handsUpStateList.indexOf(element)
                    if (index > -1) {
                        handsUpStateList[index] = element
                    } else {
                        handsUpStateList.add(element)
                    }
                }
            } else {
                handsUpStateList.clear()
                handsUpStateList.addAll(updateList)
            }
            if (increment && updateList.isNullOrEmpty()) {
                return
            }
            handsUpStateLD.postValue(updateList)
        }
    }

    override fun updateMemberLeave(list: MutableList<NEEduMember>) {
        synchronized(this) {
            handsUpStateList.removeAll(list).also {
                if (it) {
                    handsUpStateLD.postValue(null)
                }
            }
        }
    }

    override fun handsUpStateChange(state: Int, userUuid: String): LiveData<NEResult<Void>> {
        val req = NEEduUpdateMemberPropertyReq(value = state)
        return UserServiceRepository.updateProperty(
            NEEduManagerImpl.getRoom().roomUuid,
            userUuid,
            NEEduMemberPropertiesType.AVHANDSUP.type,
            req
        )
    }

    override fun onHandsUpStateChange(): LiveData<List<NEEduMember>?> {
        return handsUpStateLD
    }

    override fun updateHandsUpState(member: NEEduMember) {
        synchronized(this) {
            member.properties?.avHandsUp?.let {
                // adjust hands up members list & post data
                var index = handsUpStateList.indexOf(member)
                if (index > -1) {
                    handsUpStateList[index] = member
                } else {
                    handsUpStateList.add(member)
                }
                // post member change
                handsUpStateLD.postValue(listOf(member))
            }
        }
    }
}
/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.netease.yunxin.app.wisdom.edu.logic.impl.NEEduManagerImpl
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMemberProperties
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduStreams
import com.netease.yunxin.app.wisdom.edu.logic.service.NEEduMemberService

/**
 * Created by hzsunyj on 2021/5/20.
 */
internal class NEEduMemberServiceImpl : NEEduMemberService() {

    /**
     * 房间内所有成员
     */
    private val joinList: MutableList<NEEduMember> = mutableListOf()

    private val joinLD: MediatorLiveData<List<NEEduMember>> = MediatorLiveData()

    private val leaveLD: MediatorLiveData<List<NEEduMember>> = MediatorLiveData()

    private val propertiesChangeLD: MediatorLiveData<Pair<NEEduMember, NEEduMemberProperties>> = MediatorLiveData()

    override fun getMemberList(): MutableList<NEEduMember> {
        return joinList
    }

    override fun getLocalUser(): NEEduMember? {
        return joinList.firstOrNull { NEEduManagerImpl.getEntryMember() == it } ?: NEEduManagerImpl.getEntryMember()
    }

    override fun updateMemberPropertiesChange(member: NEEduMember, properties: NEEduMemberProperties) {
        propertiesChangeLD.postValue(Pair(member, properties))
    }

    /**
     * 更新本地成员列表
     *
     * @param list 更新成员
     * @param increment 是否增量更新
     */
    override fun updateMemberJoin(list: List<NEEduMember>, increment: Boolean) {
        synchronized(joinList) {
            if (increment) {
                for (element in list) {
                    var index = joinList.indexOf(element)
                    if (index > -1) {
                        joinList[index] = element
                    } else {
                        joinList.add(element)
                    }
                }
            } else {
                joinList.clear()
                joinList.addAll(list)
            }
            joinList.sortWith(compareBy({ !it.isHost() }, { NEEduManagerImpl.getEntryMember().userUuid != it.userUuid }))
        }
        joinLD.postValue(joinList)
    }

    override fun onMemberJoin(): LiveData<List<NEEduMember>> {
        return joinLD
    }

    override fun updateMemberLeave(list: List<NEEduMember>) {
        synchronized(joinList) {
            joinList.removeAll(list).also {
                leaveLD.postValue(list)
                joinLD.postValue(joinList)
            }
        }
    }

    override fun onMemberLeave(): LiveData<List<NEEduMember>> {
        return leaveLD
    }


    override fun onMemberPropertiesChange(): LiveData<Pair<NEEduMember, NEEduMemberProperties>> {
        return propertiesChangeLD
    }

    override fun updateStreamChange(member: NEEduMember, streams: NEEduStreams): NEEduMember? {
        for (element in joinList) {
            if (element.userUuid == member.userUuid) {
                element.streams = element.streams?.merge(streams) ?: streams
                return element
            }
        }
        return null
    }

    override fun updateStreamRemove(member: NEEduMember, streamType: String): NEEduMember? {
        for (element in joinList) {
            if (element.userUuid == member.userUuid) {
                element.streams = element.streams?.delete(streamType) ?: null
                return element
            }
        }
        return null
    }

    /**
     * 更新成员属性的本地缓存
     *
     * @param member 变更成员
     * @param properties 变更属性
     * @return 发生变化的成员
     */
    override fun updateMemberPropertiesCache(member: NEEduMember, properties: NEEduMemberProperties): NEEduMember? {
        for (element in joinList) {
            if (member == element) {
                element.properties = element.properties?.merge(properties) ?: properties
                return element
            }
        }
        return null
    }
}
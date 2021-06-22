/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMemberProperties
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduStreams

/**
 * 提供可供 App 调用的管理课堂成员相关方法
 *
 */
abstract class NEEduMemberService : INEEduService() {
    /**
     * 获取当前课堂成员详情
     */
    abstract fun getMemberList(): MutableList<NEEduMember>

    /**
     * 变更成员加入
     * @param increment true 表示增量，false表示全量
     */
    internal abstract fun updateMemberJoin(list: MutableList<NEEduMember>, increment: Boolean)

    /**
     * 人员在线/离线状态发生变化
     */
    abstract fun onMemberJoin(): LiveData<List<NEEduMember>>

    /**
     * 变更成员离开
     */
    internal abstract fun updateMemberLeave(list: MutableList<NEEduMember>)

    /**
     * 人员在线/离线状态发生变化
     */
    abstract fun onMemberLeave(): LiveData<List<NEEduMember>>

    /**
     * 成员流的变更
     */
    internal abstract fun updateStreamChange(member: NEEduMember, streams: NEEduStreams): NEEduMember?


    /**
     * 成员流的删除
     */
    internal abstract fun updateStreamRemove(member: NEEduMember, streamType: String): NEEduMember?

    /**
     * 更新成员属性的本地缓存
     */
    internal abstract fun updateMemberPropertiesCache(
        member: NEEduMember,
        properties: NEEduMemberProperties,
    ): NEEduMember?

    /**
     * 获取当前用户的最新数据
     *
     * @return
     */
    abstract fun getLocalUser(): NEEduMember?

    /**
     * 成员属性变更
     */
    internal abstract fun updateMemberPropertiesChange(member: NEEduMember, properties: NEEduMemberProperties)

    abstract fun onMemberPropertiesChange(): LiveData<Pair<NEEduMember, NEEduMemberProperties>>
}

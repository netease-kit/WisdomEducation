/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember

/**
 * 提供可供 App 调用的上台相关方法
 *
 */
abstract class NEEduHandsUpService : INEEduService() {

    /**
     * 获取当前举手中的成员详情
     */
    abstract fun getHandsUpApplyList(): MutableList<NEEduMember>

    /**
     * 获取当前台上成员详情
     */
    abstract fun getOnStageMemberList(): MutableList<NEEduMember>

    /**
     * 改变成员的举手状态
     */
    abstract fun handsUpStateChange(state: Int, userUuid: String): LiveData<NEResult<Void>>

    /**
     * 学生台上状态发生变化
     */
    abstract fun onHandsUpStateChange(): LiveData<List<NEEduMember>?>

    /**
     * 学生举手状态发生变化 下行
     */
    internal abstract fun updateHandsUpState(member: NEEduMember)
    /**
     * 变更成员加入
     * @param increment true 表示增量，false表示全量
     */
    internal abstract fun updateMemberJoin(list: MutableList<NEEduMember>, increment: Boolean)
    /**
     * 变更成员离开
     */
    internal abstract fun updateMemberLeave(list: MutableList<NEEduMember>)
}

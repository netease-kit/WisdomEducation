/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember

/**
 * Methods for speakers
 *
 */
abstract class NEEduHandsUpService : INEEduService() {

    /**
     * Get the profiles of members who raise a hand
     */
    abstract fun getHandsUpApplyList(): MutableList<NEEduMember>

    /**
     * Get the profiles of members as speaker
     */
    abstract fun getOnStageMemberList(): MutableList<NEEduMember>

    /**
     * Change the hand-up state
     */
    abstract fun handsUpStateChange(state: Int, userUuid: String): LiveData<NEResult<Void>>

    /**
     * Hands-up state changes
     */
    abstract fun onHandsUpStateChange(): LiveData<List<NEEduMember>?>

    /**
     * Ppdate Hand-up State
     */
    internal abstract fun updateHandsUpState(member: NEEduMember)
    /**
     * Update the member list after members join the room
     * @param increment true indicates increment，false indicates full load
     */
    internal abstract fun updateMemberJoin(list: MutableList<NEEduMember>, increment: Boolean)
    /**
     * update the member list after members leave the room
     */
    internal abstract fun updateMemberLeave(list: MutableList<NEEduMember>)
}

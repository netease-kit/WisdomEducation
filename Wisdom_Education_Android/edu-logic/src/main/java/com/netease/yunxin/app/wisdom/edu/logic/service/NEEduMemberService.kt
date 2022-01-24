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
 * Methods used to manage members in the class
 *
 */
abstract class NEEduMemberService : INEEduService() {
    /**
     * Get the details of members in the class
     */
    abstract fun getMemberList(): MutableList<NEEduMember>

    /**
     * Update the member list if new members join the class
     * @param increment true indicates increment load and false indicates full load.
     */
    abstract fun updateMemberJoin(list: List<NEEduMember>, increment: Boolean)

    /**
     * Status changes if the member joins the room
     */
    abstract fun onMemberJoin(): LiveData<List<NEEduMember>>

    /**
     * Update the member list if members leave the room
     */
    abstract fun updateMemberLeave(list: List<NEEduMember>)

    /**
     * Status changes if the member leaves the room
     */
    abstract fun onMemberLeave(): LiveData<List<NEEduMember>>

    /**
     * Update the stream changes
     */
    internal abstract fun updateStreamChange(member: NEEduMember, streams: NEEduStreams): NEEduMember?


    /**
     * Delete the member stream
     */
    internal abstract fun updateStreamRemove(member: NEEduMember, streamType: String): NEEduMember?

    /**
     * Update the local cache of member properties
     */
    internal abstract fun updateMemberPropertiesCache(
        member: NEEduMember,
        properties: NEEduMemberProperties,
    ): NEEduMember?

    /**
     * Get the current member
     *
     * @return
     */
    abstract fun getLocalUser(): NEEduMember?

    /**
     * Update member properties
     */
    internal abstract fun updateMemberPropertiesChange(member: NEEduMember, properties: NEEduMemberProperties)

    /**
     * Observe member property changes
     */
    abstract fun onMemberPropertiesChange(): LiveData<Pair<NEEduMember, NEEduMemberProperties>>
}

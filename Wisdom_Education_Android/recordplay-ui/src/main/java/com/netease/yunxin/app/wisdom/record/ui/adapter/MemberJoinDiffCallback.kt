/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.netease.yunxin.app.wisdom.record.actor.NERecordVideoActor


class MemberJoinDiffCallback(
    private val oldVideoActorList: List<NERecordVideoActor>,
    private val newVideoActorList: List<NERecordVideoActor>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldVideoActorList.size
    }

    override fun getNewListSize(): Int {
        return newVideoActorList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldVideoActorList[oldItemPosition].recordItem == newVideoActorList[newItemPosition].recordItem
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldVideoActor: NERecordVideoActor = oldVideoActorList[oldItemPosition]
        val newVideoActor: NERecordVideoActor = newVideoActorList[newItemPosition]
        return oldVideoActor == newVideoActor
    }
}

/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.adapter

import android.view.ViewGroup
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.base.BaseClassActivity
import com.netease.yunxin.app.wisdom.edu.ui.clazz.widget.RtcVideoAudioView
import com.netease.yunxin.app.wisdom.rvadapter.BaseAdapter
import com.netease.yunxin.app.wisdom.rvadapter.BaseDelegate
import com.netease.yunxin.app.wisdom.rvadapter.BaseViewHolder
import com.netease.yunxin.app.wisdom.rvadapter.OnItemClickListener

class MemberVideoListAdapter(
    val activity: BaseClassActivity,
    dataList: MutableList<NEEduMember>,
    listener: OnItemClickListener<NEEduMember>? = null,
) : BaseAdapter<NEEduMember>(dataList, listener) {
    init {
        setDelegate(object : BaseDelegate<NEEduMember>() {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder<*>? {
                val rtcVideoAudioView = RtcVideoAudioView(activity)
                var viewHolder = MemberViewHolder(rtcVideoAudioView)
                this@MemberVideoListAdapter.bindViewClickListener(viewHolder, viewType)
                return viewHolder
            }

            override fun getItemViewType(data: NEEduMember, pos: Int): Int {
                return 0
            }
        })
        addChildClickViewIds(R.id.video_container)
    }

    inner class MemberViewHolder(val view: RtcVideoAudioView) : BaseViewHolder<NEEduMember>(view) {
        override fun findViews() {

        }

        override fun onBindViewHolder(item: NEEduMember) {
            onBindViewHolder(item, null)
        }

        override fun onBindViewHolder(item: NEEduMember, payloads: MutableList<Any>?) {
            updateRtcView(view, item, payloads == null || payloads.isEmpty() || payloads[0] == true)
            view.enableAudio(item.hasAudio())
            view.enableVideo(item.hasVideo())
            view.enableWhiteboard(item.isGrantedWhiteboard() && !item.isHost())
        }
    }

    private fun updateRtcView(rtcVideoAudioView: RtcVideoAudioView?, member: NEEduMember, updateVideo: Boolean) {
        activity.updateRtcView(rtcVideoAudioView, member, updateVideo)
    }

    fun setData(list: List<NEEduMember>) {
        this.updateDataAndNotify(list)
    }
}


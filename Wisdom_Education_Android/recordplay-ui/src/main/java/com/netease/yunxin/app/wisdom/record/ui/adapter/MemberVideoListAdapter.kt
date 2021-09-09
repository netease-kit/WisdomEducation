/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.ui.adapter

import android.view.ViewGroup
import com.netease.yunxin.app.wisdom.record.actor.NERecordVideoActor
import com.netease.yunxin.app.wisdom.record.ui.NERecordActivity
import com.netease.yunxin.app.wisdom.record.ui.R
import com.netease.yunxin.app.wisdom.record.ui.widget.NERecordVideoView
import com.netease.yunxin.app.wisdom.rvadapter.BaseAdapter
import com.netease.yunxin.app.wisdom.rvadapter.BaseDelegate
import com.netease.yunxin.app.wisdom.rvadapter.BaseViewHolder
import com.netease.yunxin.app.wisdom.rvadapter.OnItemClickListener

class MemberVideoListAdapter(
    val activity: NERecordActivity,
    dataList: MutableList<NERecordVideoActor>,
    listener: OnItemClickListener<NERecordVideoActor>? = null,
) : BaseAdapter<NERecordVideoActor>(dataList, listener) {
    init {
        setDelegate(object : BaseDelegate<NERecordVideoActor>() {
            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BaseViewHolder<*>? {
                val videoView = NERecordVideoView(activity)
                var viewHolder = MemberViewHolder(videoView)
                this@MemberVideoListAdapter.bindViewClickListener(viewHolder, viewType)
                return viewHolder
            }

            override fun getItemViewType(data: NERecordVideoActor, pos: Int): Int {
                return 0
            }
        })
        addChildClickViewIds(R.id.ic_audio, R.id.ic_video, R.id.video_container)
    }

    inner class MemberViewHolder(val view: NERecordVideoView) : BaseViewHolder<NERecordVideoActor>(view) {
        override fun findViews() {

        }

        override fun onBindViewHolder(item: NERecordVideoActor) {
            onBindViewHolder(item, null)
        }

        override fun onBindViewHolder(item: NERecordVideoActor, payloads: MutableList<Any>?) {
            updateRtcView(view, item, payloads == null || payloads.isEmpty() || payloads[0] == true)
            view.enableAudio(item.enabledAudio)
            view.enableVideo(item.enableVideo)
//            view.enableScreenShare(item.enableScreenShare)
        }
    }

    private fun updateRtcView(rtcVideoAudioView: NERecordVideoView, videoActor: NERecordVideoActor, updateVideo: Boolean) {
        rtcVideoAudioView.updateView(videoActor)
        if(updateVideo) activity.renderVideo(rtcVideoAudioView.getViewContainer(), videoActor)
    }

    fun setData(list: List<NERecordVideoActor>) {
        this.updateDataAndNotify(list)
    }
}


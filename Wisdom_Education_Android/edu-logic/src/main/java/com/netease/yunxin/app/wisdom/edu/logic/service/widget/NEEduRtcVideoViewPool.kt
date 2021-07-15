/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.service.widget

import android.view.ViewGroup
import android.widget.LinearLayout
import com.netease.lava.api.IVideoRender
import com.netease.lava.nertc.sdk.video.NERtcVideoView
import com.netease.yunxin.app.wisdom.edu.logic.NEEduManager
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduMember
import java.util.*

class NEEduRtcVideoViewPool {
    companion object {
        private val rtcVideoMap = mutableMapOf<Long, NERtcVideoView?>()
        private val rtcSubVideoMap = mutableMapOf<Long, NERtcVideoView?>()
        private val queue = LinkedList<NERtcVideoView>()

        fun recycleRtcVideo(rtcUid: Long) {
            val neRtcVideoView = rtcVideoMap[rtcUid]
            removeSelf(neRtcVideoView)
            rtcVideoMap[rtcUid] = null
        }

        fun obtainRtcVideo(rtcUid: Long): NERtcVideoView {
            var videoView = rtcVideoMap[rtcUid]
            videoView = pickVideoView(videoView)
            rtcVideoMap[rtcUid] = videoView
            return videoView
        }

        fun recycleRtcSubVideo(rtcUid: Long) {
            val neRtcVideoView = rtcSubVideoMap[rtcUid]
            removeSelf(neRtcVideoView)
            rtcSubVideoMap[rtcUid] = null
        }

        fun obtainRtcSubVideo(rtcUid: Long): NERtcVideoView {
            var videoView = rtcSubVideoMap[rtcUid]
            videoView = pickVideoView(videoView).apply {
                setScalingType(IVideoRender.ScalingType.SCALE_ASPECT_FIT)
            }
            rtcSubVideoMap[rtcUid] = videoView
            return videoView
        }

        fun clear() {
            queue.clear()
            rtcVideoMap.clear()
            rtcSubVideoMap.clear()
        }

        private fun removeSelf(neRtcVideoView: NERtcVideoView?) {
            if (neRtcVideoView != null) {
                if (neRtcVideoView.parent != null) {
                    (neRtcVideoView.parent as ViewGroup).removeView(neRtcVideoView)
                }
                queue.add(neRtcVideoView)
            }
        }

        private fun pickVideoView(videoView: NERtcVideoView?): NERtcVideoView {
            var videoView1 = videoView
            if (videoView1 == null) {
                videoView1 = queue.poll()
            }
            if (videoView1 == null) {
                videoView1 = NERtcVideoView(NEEduManager.context).apply {
                    setZOrderMediaOverlay(true)
                    layoutParams = ViewGroup.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }
            }
            if (videoView1.parent != null) {
                (videoView1.parent as ViewGroup).removeView(videoView1)
            }
            return videoView1
        }

        fun batchRecycleWithoutKeepMember(list: MutableList<NEEduMember>) {
            synchronized(rtcVideoMap) {
                for ((key, _) in rtcVideoMap) {
                    val firstOrNull = list.firstOrNull { it.rtcUid == key }
                    if (firstOrNull == null) {
                        recycleRtcVideo(key)
                    }
                }
            }
            synchronized(rtcSubVideoMap) {
                for ((key, _) in rtcSubVideoMap) {
                    val firstOrNull = list.firstOrNull { it.rtcUid == key }
                    if (firstOrNull == null) {
                        recycleRtcSubVideo(key)
                    }
                }
            }
        }
    }
}
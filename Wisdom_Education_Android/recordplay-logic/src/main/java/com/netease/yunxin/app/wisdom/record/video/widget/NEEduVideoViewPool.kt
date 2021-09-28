/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.video.widget

import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.netease.yunxin.app.wisdom.player.sdk.view.AdvanceTextureView
import com.netease.yunxin.app.wisdom.record.NERecordPlayer

class NEEduVideoViewPool {
    companion object {
        private val videoMap = mutableMapOf<Long, AdvanceTextureView?>()
        private val subVideoMap = mutableMapOf<Long, AdvanceTextureView?>()

        fun recycleVideo(rtcUid: Long) {
            val neVideoView = videoMap[rtcUid]
            removeSelf(neVideoView)
            videoMap[rtcUid] = null
        }

        fun obtainVideo(rtcUid: Long): AdvanceTextureView {
            var videoView = videoMap[rtcUid]
            videoView = pickVideoView(videoView)
            videoMap[rtcUid] = videoView
            return videoView
        }

        fun recycleSubVideo(rtcUid: Long) {
            val videoView = subVideoMap[rtcUid]
            removeSelf(videoView)
            subVideoMap[rtcUid] = null
        }

        fun obtainSubVideo(rtcUid: Long): AdvanceTextureView {
            var videoView = subVideoMap[rtcUid]
            videoView = pickVideoView(videoView)
            subVideoMap[rtcUid] = videoView
            return videoView
        }

        fun clear() {
            videoMap.clear()
            subVideoMap.clear()
        }

        private fun removeSelf(videoView: AdvanceTextureView?) {
            if (videoView != null) {
                if (videoView.parent != null) {
                    (videoView.parent as ViewGroup).removeView(videoView)
                }
            }
        }

        private fun pickVideoView(videoView: AdvanceTextureView?): AdvanceTextureView {
            var videoView1 = videoView
            if (videoView1 == null) {
                videoView1 = AdvanceTextureView(NERecordPlayer.context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER
                    )
                }
            }
            if (videoView1.parent != null) {
                (videoView1.parent as ViewGroup).removeView(videoView1)
            }
            return videoView1
        }
    }
}
/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.widget

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.netease.lava.api.model.RTCNetworkStatusType
import com.netease.lava.nertc.sdk.stats.NERtcNetworkQualityInfo
import com.netease.yunxin.app.wisdom.base.util.TimeUtil
import com.netease.yunxin.app.wisdom.edu.ui.R
import com.netease.yunxin.app.wisdom.edu.ui.databinding.LayoutTitleViewBinding
import com.netease.yunxin.kit.alog.ALog
import kotlin.math.max

class TitleView(context: Context, attrs: AttributeSet?, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding = LayoutTitleViewBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private val updateHandler: Handler = Handler(Looper.getMainLooper())

    private var time: Long = 0

    private lateinit var prefix: String

    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            binding.tvClazzState.text = "$prefix(${getClazzDuration()})"
            time++
            updateHandler.postDelayed(this, 1000)
        }
    }

    init {
        binding.apply {
            ivQuality.setImageResource(R.drawable.ic_clazz_signal_good)
        }
    }

    fun getClazzDuration(): String {
        return TimeUtil.stringForTimeHMS(time)
    }

    /**
     * directed to the class end view
     *
     * @param duration The class duration
     */
    fun setFinishClazzState(duration: String) {
        updateHandler.removeCallbacks(updateTimeRunnable)
        binding.tvClazzState.text = context.getString(R.string.class_finish, duration)
        binding.tvClazzName.visibility = View.GONE
        binding.ivInfo.visibility = View.GONE
        binding.ivQuality.visibility = View.GONE
        binding.tvBack.visibility = View.GONE
    }

    fun setClazzName(text: String) {
        binding.tvClazzName.text = text
    }

    fun setClazzState(text: String) {
        binding.tvClazzState.text = text
        updateHandler.removeCallbacks(updateTimeRunnable)
    }

    fun startClazzState(prefix: String, time: Long) {
        this.prefix = prefix
        this.time = time / 1000
        updateHandler.removeCallbacks(updateTimeRunnable)
        updateTimeRunnable.run()
    }

    fun getBackTv(): View {
        return binding.tvBack
    }

    fun getClazzInfoBtn(): View {
        return binding.ivInfo
    }

    fun setClazzInfoClickListener(l: OnClickListener) {
        binding.ivInfo.setOnClickListener(l)
    }

    var lastUpStatus = 0
    var lastDownStatus = 0

    fun setNetworkQuality(quality: NERtcNetworkQualityInfo) {
        binding.ivQuality.apply {
            if(quality.upStatus != lastUpStatus && quality.downStatus != lastDownStatus) {
                ALog.i("setNetworkQuality upStatus:${quality.upStatus} downStatus:${quality.downStatus}")
            }
            lastUpStatus = quality.upStatus
            lastDownStatus = quality.downStatus
            when (max(quality.upStatus, quality.downStatus)) {
                RTCNetworkStatusType.kRtcNetworkStatusGood, RTCNetworkStatusType.kRtcNetworkStatusExcellent -> setImageResource(
                    R.drawable
                        .ic_clazz_signal_good)
                RTCNetworkStatusType.kRtcNetworkStatusPoor -> setImageResource(R.drawable.ic_clazz_signal_normal)
                RTCNetworkStatusType.kRtcNetworkStatusBad, RTCNetworkStatusType.kRtcNetworkStatusVeryBad -> setImageResource(
                    R.drawable
                        .ic_clazz_signal_bad)
                RTCNetworkStatusType.kRtcNetworkStatusDown, RTCNetworkStatusType.kRtcNetworkStatusUnknown -> setImageResource(R.drawable.ic_clazz_signal)
            }
        }
    }

    override fun onDetachedFromWindow() {
        updateHandler.removeCallbacks(updateTimeRunnable)
        super.onDetachedFromWindow()
    }
}
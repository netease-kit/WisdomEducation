/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.actor

import android.content.Context
import androidx.core.math.MathUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.netease.yunxin.app.wisdom.player.sdk.PlayerManager
import com.netease.yunxin.app.wisdom.player.sdk.VodPlayer
import com.netease.yunxin.app.wisdom.player.sdk.VodPlayerObserver
import com.netease.yunxin.app.wisdom.player.sdk.constant.CauseCode
import com.netease.yunxin.app.wisdom.player.sdk.model.*
import com.netease.yunxin.app.wisdom.player.sdk.view.AdvanceTextureView
import com.netease.yunxin.app.wisdom.record.NERecordPlayer
import com.netease.yunxin.app.wisdom.record.base.INERecordVideoActor
import com.netease.yunxin.app.wisdom.record.model.NERecordItem
import com.netease.yunxin.app.wisdom.record.model.NERecordPlayState
import com.netease.yunxin.kit.alog.ALog

/**
 * 时间轴音视频Actor类
 *
 */
class NERecordVideoActor : INERecordVideoActor {
    private val tag: String = "NERecordVideoActor"

    private var textureView: AdvanceTextureView? = null

    private lateinit var player: VodPlayer

    var enabledAudio = true

    var enableVideo = true

    var enableScreenShare = false

    private var playerVolume = 1f

    lateinit var context: Context

    lateinit var recordItem: NERecordItem

    private var mediaInfo: MediaInfo? = null

    private var state: Int = NERecordPlayState.IDLE

    private val playStateLD: MediatorLiveData<Int> = MediatorLiveData()

    var seekOnFirstRender = false

    companion object {

        val options by lazy {
            val options = VideoOptions()
            options.hardwareDecode = false
            /**
             * isPlayLongTimeBackground 控制退到后台或者锁屏时是否继续播放，开发者可根据实际情况灵活开发,我们的示例逻辑如下：
             * 使用软件解码：
             * isPlayLongTimeBackground 为 false 时，直播进入后台停止播放，进入前台重新拉流播放
             * isPlayLongTimeBackground 为 true 时，直播进入后台不做处理，继续播放,
             *
             * 使用硬件解码：
             * 直播进入后台停止播放，进入前台重新拉流播放
             */
            options.isPlayLongTimeBackground = false
            options.bufferStrategy = VideoBufferStrategy.ANTI_JITTER
            options.isAutoStart = true
            options
        }

    }


    fun init(context: Context, recordItem: NERecordItem) {
        this.context = context
        this.recordItem = recordItem
        player = PlayerManager.buildVodPlayer(context, recordItem.url, options)
        player.registerPlayerObserver(playerObserver, true)
        start()
    }

    fun render(textureView: AdvanceTextureView) {
        this.textureView = textureView
        player.setupRenderView(textureView, VideoScaleMode.FIT)
    }

    override fun switchAudio(enable: Boolean) {
        this.enabledAudio = enable
        player.setMute(!enable)
    }

    override fun setVolume(volume: Float) {
        this.playerVolume = volume
        player.setVolume(volume)
    }

    override fun switchVideo(enable: Boolean) {
        this.enableVideo = enable
    }

    override fun start() {
        player.start()
        // 从stopped状态返回，手动更新一次 PLAYING 状态
        if (state == NERecordPlayState.STOPPED) {
            updateState(NERecordPlayState.PLAYING)
        }
    }

    override fun pause() {
        player.pause()
    }

    override fun seek(positionMs: Long) {
        val targetPos = MathUtils.clamp(positionMs - recordItem.offset, 0, player.duration)
        ALog.i(tag, "seek positionMs: $positionMs duration: ${player.duration} target: $targetPos")
        player.seekTo(targetPos)
        // seek完成之后，直接继续播放
        if (state != NERecordPlayState.PLAYING) {
            player.start()
            // 从stopped状态seek，手动更新一次 PLAYING 状态
            if (state == NERecordPlayState.STOPPED) {
                updateState(NERecordPlayState.PLAYING)
            }
        }
    }

    override fun stop() {
        player.stop()
    }

    override fun setSpeed(speed: Float) {
        player.setPlaybackSpeed(speed)
    }

    override fun getDuration(): Long {
        return player.duration
    }

    override fun getCurrentPosition(): Long {
        return player.currentPosition
    }

    @NERecordPlayState
    override fun getState(): Int {
        return state
    }

    override fun onStateChange(): LiveData<Int> {
        return playStateLD
    }

    override fun updateState(@NERecordPlayState playState: Int) {
        state = playState
        playStateLD.postValue(playState)
    }

    /**
     * 切换播放地址
     *
     * @param url
     */
    fun switchContentUrl(url: String?) {

    }

    private val playerObserver: VodPlayerObserver = object : VodPlayerObserver {
        override fun onCurrentPlayProgress(
            currentPosition: Long,
            duration: Long,
            percent: Float,
            cachedPosition: Long
        ) {

        }

        override fun onSeekCompleted() {
            ALog.i(tag, "onSeekCompleted")
        }

        override fun onCompletion() {
        }

        override fun onAudioVideoUnsync() {
            ALog.i(tag, "Audio and video are out of sync")
        }

        override fun onNetStateBad() {}
        override fun onDecryption(ret: Int) {}
        override fun onPreparing() {}
        override fun onPrepared(info: MediaInfo) {
            ALog.i(tag, "onPrepared $info")
            mediaInfo = info
        }

        override fun onError(code: Int, extra: Int) {
            if (code == CauseCode.CODE_VIDEO_PARSER_ERROR) {
                ALog.i(tag, "Video parsing error")
            } else {
                ALog.i(tag, "Play error, error code:$code")
            }
        }

        override fun onFirstVideoRendered() {
            ALog.i(tag, "The first frame of video has been parsed")
            if (seekOnFirstRender) {
                seek(NERecordPlayer.instance.getCurrentPosition())
                seekOnFirstRender = false
            } else {
                pause()
                updateState(NERecordPlayState.PREPARED)
            }
        }

        override fun onFirstAudioRendered() {

        }

        override fun onBufferingStart() {
        }

        override fun onBufferingEnd() {
        }

        override fun onBuffering(percent: Int) {
        }

        override fun onVideoDecoderOpen(value: Int) {
            ALog.i(tag, "Use the decoder type: ${if (value == 1) "Hardware decode" else "Soft decode"}")
        }

        override fun onStateChanged(stateInfo: StateInfo?) {
            ALog.i(tag, "onStateChanged ${stateInfo?.state}")
            stateInfo?.let {
                if (it.state.ordinal != NERecordPlayState.PREPARED) updateState(it.state.ordinal)
            }
        }

        override fun onHttpResponseInfo(code: Int, header: String) {
            ALog.i(tag, "onHttpResponseInfo,code:$code header:$header")
        }
    }

    fun releasePlayer() {
        ALog.i(tag, "releasePlayer")
        player.apply {
            registerPlayerObserver(playerObserver, false)
            setupRenderView(null, VideoScaleMode.NONE)
            stop()
        }

        textureView?.releaseSurface()
        textureView = null
    }

    override fun toString(): String {
        return "NERecordVideoActor(tag='$tag', recordItem=$recordItem, state=$state)"
    }
}
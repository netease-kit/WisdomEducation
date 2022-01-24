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
 * Timeline audio and video Actor class
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

    /**
     * This flag means pause after seek is complete. Pause playback, through seek to change the progress of the way,
     * midway inserted video, will be the first frame after the completion of loading suspended,
     * but the progress is not accurate, need to wait for seek to complete and then suspended
     */
    var pauseAfterSeek = false

    companion object {

        val options by lazy {
            val options = VideoOptions()
            options.hardwareDecode = false
            /**
             * isPlayLongTimeBackground controls whether to continue playing when backing to the background or locking the screen.* Developers can flexibly develop according to the actual situation. Our example logic is as follows:
             * Use software to decode:
             * When isPlayLongTimeBackground is false, the live broadcast stops playing in the background, and starts streaming again in the foreground
             * When isPlayLongTimeBackground is true, the live broadcast enters the background and continues to play.
             *
             * Use hardware decoding:
             * Enter the background to stop the broadcast, enter the foreground to pull the stream again
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
        pauseAfterSeek = false
        // Resume from the stopped state. Mannually update the PLAYING state
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
        // Continue playback after the seek operation
        if (state == NERecordPlayState.STOPPED) {
            player.start()
            pauseAfterSeek = false
            updateState(NERecordPlayState.PLAYING)
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
     * Switching the Playing Address
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
            if (pauseAfterSeek) {
                pause()
                pauseAfterSeek = false
            }
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
                if (NERecordPlayer.instance.getState() == NERecordPlayState.PAUSED) pauseAfterSeek = true
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
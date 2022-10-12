/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.base.util.observeForeverOnce
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduHttpCode
import com.netease.yunxin.app.wisdom.record.model.NERecordItem
import com.netease.yunxin.app.wisdom.record.model.NERecordPlayState
import com.netease.yunxin.app.wisdom.record.actor.NERecordClockActor
import com.netease.yunxin.app.wisdom.record.actor.NERecordManager
import com.netease.yunxin.app.wisdom.record.actor.NERecordVideoActor
import com.netease.yunxin.app.wisdom.record.base.INERecordActor
import com.netease.yunxin.app.wisdom.record.base.INERecordControlView
import com.netease.yunxin.app.wisdom.record.event.NERecordEventHandler
import com.netease.yunxin.app.wisdom.record.listener.NERecordUIListener
import com.netease.yunxin.app.wisdom.record.net.service.RecordPlayRepository
import com.netease.yunxin.app.wisdom.record.options.NERecordOptions
import com.netease.yunxin.app.wisdom.record.video.widget.NEEduVideoViewPool
import com.netease.yunxin.kit.alog.ALog

/**
 * Recording and playback core business class
 *
 * @property recordOptions
 */
class NERecordPlayer(var recordOptions: NERecordOptions) : NERecordUIListener, INERecordActor {
    private lateinit var clockActor: NERecordClockActor
    private lateinit var controlView: INERecordControlView
    private lateinit var recordManager: NERecordManager
    private var hostActors: MutableList<NERecordVideoActor> = mutableListOf()

    companion object {
        lateinit var instance: NERecordPlayer
        lateinit var context: Application
        var audioEnable = true
        var volume = 1f
        var videoEnable = true

        /**
         * Query recording playback history and create a NERecordPlayer instance
         *
         * @param roomUuid room unique ID
         * @param rtcCid rtc ID
         * @return
         */
        fun createPlayer(
            roomUuid: String,
            rtcCid: String,
        ): LiveData<NEResult<NERecordPlayer>> {
            val managerLD: MediatorLiveData<NEResult<NERecordPlayer>> = MediatorLiveData<NEResult<NERecordPlayer>>()
            RecordPlayRepository.recordPlayback(roomUuid, rtcCid).observeForeverOnce { t ->
                if (t.success()) {
                    if (t.data == null || t.data!!.recordItemList.size == 0) {
                        destroy()
                        managerLD.postValue(NEResult(NEEduHttpCode.NO_CONTENT.code))
                    } else {
                        var roomName: String?
                        var roomUuid: String?
                        var teacherName: String? = null
                        val recordPlayer = t.data!!.let {
                            it.snapshotDto.snapshot.also { it1 ->
                                roomName = it1.room.roomName
                                roomUuid = it1.room.roomUuid
                                teacherName = it1.members.firstOrNull { it2 -> it2.isHost() }?.userName
                            }
                            NERecordPlayer(NERecordOptions(it, roomName, roomUuid, teacherName))
                        }
                        instance = recordPlayer
                        managerLD.postValue(NEResult(t.code, recordPlayer))
                    }
                } else {
                    destroy()
                    managerLD.postValue(NEResult(t.code))
                }
            }
            return managerLD
        }

        /**
         * destroy instance of NERecordPlayer
         *
         */
        fun destroy() {
            if(this::instance.isInitialized) {
                instance.stop()
                instance.release()
            }
        }
    }

    /**
     * record player initialize
     *
     * @param application context
     * @param controlView record player UI update interface
     */
    fun init(application: Application, controlView: INERecordControlView) {
        context = application
        this.controlView = controlView
        clockActor = NERecordClockActor(recordOptions, this)
        recordManager = NERecordManager(clockActor).apply {
            init()
            onStateChange().observeForever {
//                if (it == NERecordPlayState.BUFFERING_END) seekOnBuffering(getCurrentPosition())
            }
        }
    }

    /**
     * whiteboard record list
     *
     * @return
     */
    fun getWhiteboardList(): List<NERecordItem> {
        return recordOptions.recordData.recordItemList.filter { it.type == NERecordItem.TYPE_WHITEBOARD }
    }

    /**
     * start time of record
     *
     * @return
     */
    fun getStartTime(): Long {
        return recordOptions.recordData.record.startTime
    }

    /**
     * video record list
     *
     * @return
     */
    fun getVideoList(): List<NERecordItem> {
        return recordOptions.recordData.recordItemList.filter { (it.type == NERecordItem.TYPE_VIDEO || it.type == NERecordItem.TYPE_AUDIO)
                && !it.subStream }
    }

    /**
     * sub video record list
     *
     * @return
     */
    fun getSubVideoList(): List<NERecordItem> {
        return recordOptions.recordData.recordItemList.filter { it.type == NERecordItem.TYPE_VIDEO && it.subStream }
    }

    /**
     * Added event interceptor
     *
     * @param handler event interceptor
     */
    fun addHandler(handler: NERecordEventHandler) {
        clockActor.addHandler(handler)
    }

    /**
     * add record actor to actor list
     *
     * @param actor
     */
    fun addActor(actor: INERecordActor) {
        recordManager.addActor(actor)
    }

    /**
     * remove record actor from actor list
     *
     * @param actor
     */
    fun removeActor(actor: INERecordActor) {
        recordManager.removeActor(actor)
    }

    /**
     * get target actor from actor list
     *
     * @param recordItem
     * @return
     */
    fun getActor(recordItem: NERecordItem): INERecordActor? {
        return recordManager.getActor(recordItem)
    }

    /**
     * get target actor which has subStream or not, from actor list
     *
     * @param recordItem
     * @return
     */
    fun getActor(recordItem: NERecordItem, subStream: Boolean): INERecordActor? {
        return recordManager.getActor(recordItem, subStream)
    }

    /**
     * add host actor to host actors list
     *
     * @param actor
     */
    fun addHostActor(actor: NERecordVideoActor) {
        hostActors.add(actor)
    }

    /**
     * get host actor from host actors list
     *
     * @return
     */
    fun getHostActors(): List<NERecordVideoActor> {
        return hostActors
    }

    /**
     * Event preprocessing
     *
     */
    fun prepareEvent() {
        recordManager.prepareEvent()
    }

    /**
     * Update actor count
     *
     * @param actorCount
     */
    fun setActorCount(actorCount: Int) {
        ALog.i("setActorCount $actorCount")
        recordManager.actorCount = actorCount
    }

    /**
     * Get actor count
     *
     * @return
     */
    fun getActorCount(): Int {
        return recordManager.actorCount
    }

    override fun onStart() {
        controlView.start()
    }

    override fun onPause() {
        controlView.pause()
    }

    override fun onProgressChanged(currentTime: Long, totalTime: Long) {
        val percent = currentTime.toFloat() / totalTime
        controlView.setProgress(percent)
    }

    override fun onStop() {
        controlView.stop()
    }

    override fun onSwitchAudio(audioEnable: Boolean) {
        controlView.switchAudio(audioEnable)
    }

    override fun onVolumeChange(volume: Float) {
        controlView.setVolume(volume)
    }

    /**
     * start playback
     *
     */
    override fun start() {
        recordManager.start()
    }

    /**
     * pause playback
     *
     */
    override fun pause() {
        recordManager.pause()
    }

    /**
     * seek playback progress
     *
     * @param positionMs
     */
    override fun seek(positionMs: Long) {
        recordManager.seek(positionMs)
    }

    /**
     * stop playback
     *
     */
    override fun stop() {
        recordManager.stop()
    }

    /**
     * change playback speed
     *
     * @param speed
     */
    override fun setSpeed(speed: Float) {
        recordManager.setSpeed(speed)
    }

    /**
     * Get playback Duration
     *
     * @return
     */
    override fun getDuration(): Long {
        return recordManager.getDuration()
    }

    /**
     * get current playback position
     *
     * @return current playback position
     */
    override fun getCurrentPosition(): Long {
        return recordManager.getCurrentPosition()
    }

    /**
     * get current playback state
     *
     * @return current playback state
     */
    @NERecordPlayState
    override fun getState(): Int {
        return recordManager.getState()
    }

    /**
     * observe playback state changess
     *
     * @return
     */
    override fun onStateChange(): LiveData<Int> {
        return recordManager.onStateChange()
    }

    /**
     * @suppress
     *
     * @param playState
     */
    override fun updateState(playState: Int) {

    }

    /**
     * switch audio
     *
     * @param audioEnable enable or disable
     */
    fun switchAudio(audioEnable: Boolean) {
        recordManager.switchAudio(!audioEnable)
        NERecordPlayer.audioEnable = !audioEnable
    }

    /**
     * Adjust the volume
     *
     * @param volume
     */
    fun setVolume(volume: Float) {
        recordManager.setVolume(volume)
        NERecordPlayer.volume = volume
    }

    /**
     * Switch video
     *
     * @param videoEnable enable or disable
     */
    fun switchVideo(videoEnable: Boolean) {
        recordManager.switchVideo(!videoEnable)
        NERecordPlayer.videoEnable = !videoEnable
    }

    /**
     * release resources related to the player
     *
     */
    fun release() {
        recordManager.removeAllActor()
        NEEduVideoViewPool.clear()
    }
}
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

        fun fetchRecord(
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
                        var roomUuid1: String?
                        var teacherName: String? = null
                        val instanceRecordPlayer = t.data!!.let {
                            it.snapshotDto.snapshot.also { it1 ->
                                roomName = it1.room.roomName
                                roomUuid1 = it1.room.roomUuid
                                teacherName = it1.members.firstOrNull { it2 -> it2.isHost() }?.userName
                            }
                            NERecordPlayer(NERecordOptions(it, roomName, roomUuid1, teacherName))
                        }
                        instance = instanceRecordPlayer
                        managerLD.postValue(NEResult(t.code, instanceRecordPlayer))
                    }
                } else {
                    destroy()
                    managerLD.postValue(NEResult(t.code))
                }
            }
            return managerLD
        }

        fun destroy() {
            if(this::instance.isInitialized) {
                instance.stop()
                instance.release()
            }
        }
    }

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

    fun getWhiteboardList(): List<NERecordItem> {
        return recordOptions.recordData.recordItemList.filter { it.type == NERecordItem.TYPE_WHITEBOARD }
    }

    fun getStartTime(): Long {
        return recordOptions.recordData.record.startTime
    }

    fun getVideoList(): List<NERecordItem> {
        return recordOptions.recordData.recordItemList.filter { it.type == NERecordItem.TYPE_VIDEO && !it.subStream }
    }

    fun getSubVideoList(): List<NERecordItem> {
        return recordOptions.recordData.recordItemList.filter { it.type == NERecordItem.TYPE_VIDEO && it.subStream }
    }

    /**
     * 新增事件拦截器
     *
     * @param handler 事件拦截器
     */
    fun addHandler(handler: NERecordEventHandler) {
        clockActor.addHandler(handler)
    }

    fun addActor(actor: INERecordActor) {
        recordManager.addActor(actor)
    }

    fun removeActor(actor: INERecordActor) {
        recordManager.removeActor(actor)
    }

    fun getActor(recordItem: NERecordItem): INERecordActor? {
        return recordManager.getActor(recordItem)
    }

    fun getActor(recordItem: NERecordItem, subStream: Boolean): INERecordActor? {
        return recordManager.getActor(recordItem, subStream)
    }

    fun addHostActor(actor: NERecordVideoActor) {
        hostActors.add(actor)
    }

    fun getHostActors(): List<NERecordVideoActor> {
        return hostActors
    }

    fun prepareEvent() {
        recordManager.prepareEvent()
    }

    fun setActorCount(actorCount: Int) {
        ALog.i("setActorCount $actorCount")
        recordManager.actorCount = actorCount
    }

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


    override fun start() {
        recordManager.start()
    }

    override fun pause() {
        recordManager.pause()
    }

    override fun seek(positionMs: Long) {
        recordManager.seek(positionMs)
    }

    override fun stop() {
        recordManager.stop()
    }

    override fun setSpeed(speed: Float) {
        recordManager.setSpeed(speed)
    }

    override fun getDuration(): Long {
        return recordManager.getDuration()
    }

    override fun getCurrentPosition(): Long {
        return recordManager.getCurrentPosition()
    }

    @NERecordPlayState
    override fun getState(): Int {
        return recordManager.getState()
    }

    override fun onStateChange(): LiveData<Int> {
        return recordManager.onStateChange()
    }

    override fun updateState(playState: Int) {
//        recordManager.updateState(playState)
    }

    fun switchAudio(audioEnable: Boolean) {
        recordManager.switchAudio(!audioEnable)
        NERecordPlayer.audioEnable = !audioEnable
    }

    fun setVolume(volume: Float) {
        recordManager.setVolume(volume)
        NERecordPlayer.volume = volume
    }

    fun switchVideo(videoEnable: Boolean) {
        recordManager.switchVideo(!videoEnable)
        NERecordPlayer.videoEnable = !videoEnable
    }

    fun release() {
        recordManager.removeAllActor()
        NEEduVideoViewPool.clear()
    }
}
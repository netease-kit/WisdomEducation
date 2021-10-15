/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.ui

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.netease.neliveplayer.proxy.config.NEPlayerConfig
import com.netease.yunxin.app.wisdom.base.util.CommonUtil.throttleFirst
import com.netease.yunxin.app.wisdom.record.NERecordPlayer
import com.netease.yunxin.app.wisdom.record.actor.NERecordVideoActor
import com.netease.yunxin.app.wisdom.record.actor.NERecordWhiteboardActor
import com.netease.yunxin.app.wisdom.record.base.INERecordControlView
import com.netease.yunxin.app.wisdom.record.event.NERecordEventHandlerCallback
import com.netease.yunxin.app.wisdom.record.event.NERecordHandsUpHandler
import com.netease.yunxin.app.wisdom.record.event.NERecordMemberHandler
import com.netease.yunxin.app.wisdom.record.event.NERecordSubVideoHandler
import com.netease.yunxin.app.wisdom.record.model.NERecordItem
import com.netease.yunxin.app.wisdom.record.model.NERecordPlayState
import com.netease.yunxin.app.wisdom.record.ui.adapter.MemberJoinDiffCallback
import com.netease.yunxin.app.wisdom.record.ui.adapter.MemberVideoListAdapter
import com.netease.yunxin.app.wisdom.record.ui.databinding.ActivityRecordplayBinding
import com.netease.yunxin.app.wisdom.player.sdk.PlayerManager
import com.netease.yunxin.app.wisdom.player.sdk.model.SDKInfo
import com.netease.yunxin.app.wisdom.player.sdk.model.SDKOptions
import com.netease.yunxin.app.wisdom.record.video.widget.NEEduVideoViewPool
import com.netease.yunxin.app.wisdom.record.whiteboard.config.NERecordWhiteboardConfig
import com.netease.yunxin.app.wisdom.record.whiteboard.view.NERecordWhiteboardView
import com.netease.yunxin.app.wisdom.rvadapter.BaseAdapter
import com.netease.yunxin.app.wisdom.viewbinding.viewBinding
import com.netease.yunxin.kit.alog.ALog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


class NERecordActivity : AppCompatActivity(R.layout.activity_recordplay), INERecordControlView,
    BaseAdapter.OnItemChildClickListener<NERecordVideoActor>, NERecordSubVideoHandler.NERecordSubVideoHandlerCallback,
    NERecordEventHandlerCallback {
    private lateinit var memberVideoAdapter: MemberVideoListAdapter
    private val videoActorList: LinkedList<NERecordVideoActor> = LinkedList()
    private val tag: String = "NERecordActivity"
    private val binding: ActivityRecordplayBinding by viewBinding(R.id.one_container)
    private val whiteboardFragment = NERecordWhiteboardFragment()
    private lateinit var recordPlayer: NERecordPlayer
    private var isSeeking = AtomicBoolean(false)

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, NERecordActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)// keep screen on
        recordPlayer = NERecordPlayer.instance
        recordPlayer.init(application, this)
        initVideoPlayer()
        initViews()
    }

    override fun initViews() {
        var actorCount = 0
        // 白板
        recordPlayer.getWhiteboardList().takeIf { it.isNotEmpty() }?.let {
            replaceFragment(R.id.layout_whiteboard, whiteboardFragment)
            actorCount++
        }

        // 屏幕共享
        recordPlayer.getSubVideoList().takeIf { it.isNotEmpty() }?.let {
            recordPlayer.addHandler(NERecordSubVideoHandler(it, this))
        }


        // 右侧rtc列表
        recordPlayer.getVideoList().takeIf { it.isNotEmpty() }?.let {
            val rcvMemberVideo = binding.rcvMemberVideo
            val layoutManager = LinearLayoutManager(this)
            rcvMemberVideo.layoutManager = layoutManager
            rcvMemberVideo.addItemDecoration(
                MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.common_dp_4))
            )
            memberVideoAdapter = MemberVideoListAdapter(this, mutableListOf())
            memberVideoAdapter.setOnItemChildClickListener(this)
            rcvMemberVideo.adapter = memberVideoAdapter

            val initVideoList = it.filter { it1 ->
                // 过滤一开始不在台上的人
                recordPlayer.recordOptions.recordData.run {
                    (is1V1() || isSmall() || it1.isHost()) && snapshotDto.snapshot.members.any { it2 -> it2.rtcUid == it1.roomUid }
                }
            }
            initVideoList.map { it1 ->
                val videoActor = updateVideo(it1)
                if (it1.isHost()) recordPlayer.addHostActor(videoActor)
                videoActor
            }.apply {
                videoActorList.addAll(this)
            }
            recordPlayer.addHandler(NERecordMemberHandler(it, initVideoList, this))
            recordPlayer.addHandler(NERecordHandsUpHandler(it, initVideoList, this))
            recordPlayer.prepareEvent()
            videoActorList.sortWith(compareBy { it1 -> !it1.recordItem.isHost() })
            memberVideoAdapter.setData(videoActorList)

            actorCount += videoActorList.size
        }
        recordPlayer.setActorCount(actorCount)


        // 课程信息面板
        val option = recordPlayer.recordOptions
        binding.titleLayout.apply {
            option.roomName?.let { setClazzName(it) }
            setClazzState(context.getString(R.string.playback))
            setClazzInfoClickListener {
                binding.clazzInfoView.let {
                    option.roomName?.let { it1 -> it.setRoomName(it1) }
                    option.teacherName?.let { it1 -> it.setTeacherName(it1) }
                    option.roomUuid?.let { it3 ->
                        it.setRoomId(it3)
                        it.setOnCopyText(it3)
                    }
                    it.show()
                }
            }
        }

        binding.bottomView.getPlayBtn().setOnClickListener(onClickListener)
        binding.bottomView.getMuteBtn().setOnClickListener(onClickListener)
        binding.bottomView.getSeekBar().setOnSeekBarChangeListener(progressSeekListener)
        binding.volumeView.getControllerView().setOnSeekBarChangeListener(volumeListener)
        binding.titleLayout.getBackTv().setOnClickListener(onClickListener)
    }

    private fun updateVideo(recordItem: NERecordItem): NERecordVideoActor {
        recordItem.offset = recordItem.timestamp - recordPlayer.recordOptions.recordData.record.startTime
        val videoActor = NERecordVideoActor()
        videoActor.init(this, recordItem)
        recordPlayer.addActor(videoActor)
        return videoActor
    }

    fun renderVideo(
        viewGroup: ViewGroup,
        videoActor: NERecordVideoActor,
    ) {
        val recordItem = videoActor.recordItem
        val videoView = NEEduVideoViewPool.run {
            if (recordItem.subStream) obtainSubVideo(recordItem.roomUid) else obtainVideo(recordItem.roomUid)
        }
        viewGroup.removeAllViews()
        viewGroup.addView(videoView)
        videoActor.render(videoView)
    }

    private fun recycleVideo(
        viewGroup: ViewGroup?,
        videoActor: NERecordVideoActor,
    ) {
        val recordItem = videoActor.recordItem
        NEEduVideoViewPool.run {
            if (recordItem.subStream) recycleSubVideo(recordItem.roomUid) else recycleVideo(recordItem.roomUid)
        }
        viewGroup?.removeAllViews()
        videoActor.releasePlayer()
        recordPlayer.removeActor(videoActor)
    }

    fun updateWhiteBoard(webView: NERecordWhiteboardView): NERecordWhiteboardActor {
        val urls = recordPlayer.getWhiteboardList().map { it.url }
        val whiteboardActor = NERecordWhiteboardActor()
        whiteboardActor.init(webView, NERecordWhiteboardConfig("", urls, recordPlayer.getStartTime()))
        recordPlayer.addActor(whiteboardActor)
        return whiteboardActor
    }

    /**
     * ***************************** config video & subVideo *************************
     */

    private fun initVideoPlayer() {
        val config = SDKOptions()
        config.privateConfig = NEPlayerConfig()
        PlayerManager.init(this, config)
        val sdkInfo: SDKInfo = PlayerManager.getSDKInfo(this)
        ALog.i(tag, "NESDKInfo:version" + sdkInfo.version.toString() + ",deviceId:" + sdkInfo.deviceId)
    }

    /**
     * ***************************** INERecordControlView interface *************************
     */
    override fun start() {
        binding.bottomView.getPlayBtn().setImageResource(R.drawable.ic_mediacontroller_pause)
    }

    override fun pause() {
        binding.bottomView.getPlayBtn().setImageResource(R.drawable.ic_mediacontroller_play)
    }

    override fun setProgress(percent: Float) {
        if (isSeeking.get()) return
        val position = recordPlayer.getCurrentPosition()
        val duration = recordPlayer.getDuration()
        if (duration > 0) {
            val pos = 100L * position / duration
            GlobalScope.launch(Dispatchers.Main) { binding.bottomView.getProgressBar().progress = pos.toInt() }
        }
    }

    override fun stop() {
        GlobalScope.launch(Dispatchers.Main) {
            binding.bottomView.getPlayBtn().setImageResource(R.drawable.ic_mediacontroller_play)
        }
    }

    override fun switchAudio(audioEnable: Boolean) {

    }

    override fun setVolume(volume: Float) {
        binding.bottomView.getMuteBtn().setImageResource(
            if (volume > 0f) R.drawable.ic_mediacontroller_audio_enable else R.drawable.ic_mediacontroller_audio_disable
        )
    }

    override fun onSubVideo(show: Boolean, recordItem: NERecordItem?) {
        recordPlayer.getSubVideoList().takeIf { it.isNotEmpty() }?.let {
            GlobalScope.launch(Dispatchers.Main) {
                binding.layoutShareVideo.run {
                    if (show) {
                        visibility = View.VISIBLE
                        recordItem?.let {
                            updateVideo(it)
                        }?.let {
                            it.seekOnFirstRender = true
                            renderVideo(this, it)
                            recordPlayer.apply { setActorCount(getActorCount() + 1) }
                        }
                    } else {
                        visibility = View.GONE
                        recordItem?.let { recordPlayer.getActor(it) }?.let { it as NERecordVideoActor }?.let {
                            recycleVideo(this, it)
                            recordPlayer.apply { setActorCount(getActorCount() - 1) }
                        }
                    }
                }

                recordItem?.let { recordPlayer.getActor(it, false) }?.let { it as NERecordVideoActor }?.let {
                    it.enableScreenShare = show
                    memberVideoAdapter.refreshDataAndNotify(it, false)
                }

            }
        }
    }

    override fun onMemberVideoChange(inVideoList: MutableList<NERecordItem>, outVideoList: MutableList<NERecordItem>) {
        GlobalScope.launch(Dispatchers.Main) {
            val prevVideoActorList: LinkedList<NERecordVideoActor> = LinkedList()
            prevVideoActorList.addAll(videoActorList)
            recordPlayer.getVideoList().takeIf { it.isNotEmpty() }?.let {
                inVideoList.forEach {
                    it.let { it1 ->
                        updateVideo(it1).apply {
                            videoActorList.add(this)
                            ALog.i(tag, "onMemberJoin add $this")
                        }
                    }.let { it1 ->
                        it1.seekOnFirstRender = true
                        recordPlayer.apply { setActorCount(getActorCount() + 1) }
                    }
                }
                outVideoList.forEach {
                    it.let { recordPlayer.getActor(it) }?.let { it1 ->
                        (it1 as NERecordVideoActor).apply {
                            recycleVideo(null, this)
                            videoActorList.remove(this)
                            ALog.i(tag, "onMemberJoin remove $this")
                        }
                        recordPlayer.apply { setActorCount(getActorCount() - 1) }
                    }
                }
            }
            videoActorList.sortWith(compareBy { !it.recordItem.isHost() })
            memberVideoAdapter.resetData(videoActorList, false)
            ALog.i(tag, "finishMemberJoin prevVideoActorList $prevVideoActorList")
            ALog.i(tag, "finishMemberJoin videoActorList $videoActorList")
            val diffResult = DiffUtil.calculateDiff(MemberJoinDiffCallback(prevVideoActorList, videoActorList), true)
            diffResult.dispatchUpdatesTo(memberVideoAdapter)
        }
    }

    /**
     * 设置rtc列表item之间的间距
     *
     * @property spaceSize
     */
    class MarginItemDecoration(private val spaceSize: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView,
            state: RecyclerView.State,
        ) {
            with(outRect) {
                if (parent.getChildAdapterPosition(view) != 0) {
                    top = spaceSize
                }
            }
        }
    }

    override fun onItemChildClick(adapter: BaseAdapter<NERecordVideoActor>?, view: View?, position: Int) {
        when (view!!.id) {
            R.id.ic_audio -> {
                adapter?.let { it1 ->
                    it1.getItem(position).apply {
                        enabledAudio = !enabledAudio
                        switchAudio(enabledAudio)
                        it1.refreshDataAndNotify(this, false)
                    }
                }
            }
            R.id.ic_video -> {
                adapter?.let { it1 ->
                    it1.getItem(position).apply {
                        enableVideo = !enableVideo
                        switchVideo(enableVideo)
                        it1.refreshDataAndNotify(this, false)
                    }
                }
            }
            R.id.video_container -> {
                ALog.i(tag, "onItemChildClick")
            }
        }
    }

    private fun replaceFragment(id: Int, baseFragment: NERecordBaseFragment) {
        supportFragmentManager.beginTransaction().replace(id, baseFragment).commitNow()
    }

    private var onClickListener = View.OnClickListener { v ->
        when (v) {
            binding.bottomView.getPlayBtn() -> {
                when (recordPlayer.getState()) {
                    NERecordPlayState.PREPARED, NERecordPlayState.PAUSED -> recordPlayer.start()
                    NERecordPlayState.STOPPED -> {
                        binding.bottomView.getProgressBar().progress = 0
                        recordPlayer.seek(0)
                    }
                    NERecordPlayState.PLAYING -> recordPlayer.pause()
                }
            }
            binding.bottomView.getMuteBtn() -> {
                binding.volumeView.apply {
                    if (visibility == View.VISIBLE) hide() else show()
                }
            }
            binding.titleLayout.getBackTv() -> {
                onBackClicked()
            }
        }
    }.throttleFirst()

    private val progressSeekListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {}
        override fun onStartTrackingTouch(seekBar: SeekBar) {
            isSeeking.set(true)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            isSeeking.set(false)
            recordPlayer.seek(recordPlayer.getDuration() * seekBar.progress / 100)
        }
    }

    private val volumeListener: OnSeekBarChangeListener = object : OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {}
        override fun onStartTrackingTouch(seekBar: SeekBar) {}

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            recordPlayer.setVolume(seekBar.progress.toFloat() / 100)
        }
    }

    override fun onPause() {
        super.onPause()
        if (recordPlayer.getState() == NERecordPlayState.PLAYING) recordPlayer.pause()
    }

    override fun onBackPressed() {
        onBackClicked()
    }

    private fun onBackClicked() {
        NERecordPlayer.destroy()
        finish()
    }

}
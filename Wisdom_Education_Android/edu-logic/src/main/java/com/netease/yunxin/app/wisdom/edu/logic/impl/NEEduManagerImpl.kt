/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.impl

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.netease.lava.nertc.sdk.NERtcServerAddresses
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.base.network.NEEduRetrofitManager
import com.netease.yunxin.app.wisdom.base.util.observeForeverOnce
import com.netease.yunxin.app.wisdom.edu.logic.NEEduManager
import com.netease.yunxin.app.wisdom.edu.logic.cmd.CMDDispatcher
import com.netease.yunxin.app.wisdom.edu.logic.config.NEEduPrivatizationConfig
import com.netease.yunxin.app.wisdom.edu.logic.foreground.NEEduForegroundService
import com.netease.yunxin.app.wisdom.edu.logic.foreground.NEEduForegroundServiceConfig
import com.netease.yunxin.app.wisdom.edu.logic.model.*
import com.netease.yunxin.app.wisdom.edu.logic.net.service.AuthServiceRepository
import com.netease.yunxin.app.wisdom.edu.logic.net.service.BaseRepository
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduLoginRes
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduClassOptions
import com.netease.yunxin.app.wisdom.edu.logic.service.*
import com.netease.yunxin.app.wisdom.edu.logic.service.impl.*
import com.netease.yunxin.app.wisdom.edu.logic.service.widget.NEEduRtcVideoViewPool
import com.netease.yunxin.app.wisdom.im.IMManager
import com.netease.yunxin.app.wisdom.rtc.RtcManager
import com.netease.yunxin.kit.alog.ALog

/**
 *
 */
internal object NEEduManagerImpl : NEEduManager {

    private const val TAG = "EduManagerImpl"

    override lateinit var eduLoginRes: NEEduLoginRes

    lateinit var eduEntryRes: NEEduEntryRes

    override lateinit var roomConfig: NEEduRoomConfig

    val imManager: IMManager = IMManager

    val rtcManager: RtcManager = RtcManager

    override var errorLD: MediatorLiveData<Int> = MediatorLiveData()

    override fun isSelf(userUuid: String): Boolean {
        return TextUtils.equals(userUuid, getEntryMember().userUuid)
    }

    override fun getEntryMember(): NEEduEntryMember {
        return eduEntryRes.member
    }

    override fun getWbAuth(): NEEduWbAuth? {
        return getEntryMember().wbAuth
    }

    override fun getRoom(): NEEduRoom {
        return eduEntryRes.room
    }

    override fun isHost(): Boolean {
        return getEntryMember().isHost()
    }

    var cmdDispatcher: CMDDispatcher? = null

    var neEduSync: NEEduSync? = null

    private lateinit var roomService: NEEduRoomService

    private lateinit var memberService: NEEduMemberService

    private lateinit var rtcService: NEEduRtcService

    private lateinit var boardService: NEEduBoardService

    private lateinit var shareScreenService: NEEduShareScreenService

    private lateinit var handsUpServiceImpl: NEEduHandsUpService

    private lateinit var seatServiceImpl: NEEduSeatService

    private lateinit var imService: NEEduIMService

    private val observer: Observer<Boolean> = Observer<Boolean> {
        it?.let { t ->
            if (t && neEduSync != null) {// t: true indicates that re-login succeeded. If the account is logged in, the data needs to be synced
                syncSnapshot()
            }
        }
    }

    fun init(uuid: String, token: String): LiveData<NEResult<Boolean>> {
        val initLD: MediatorLiveData<NEResult<Boolean>> = MediatorLiveData()
        if (TextUtils.isEmpty(uuid) && TextUtils.isEmpty(token)) AuthServiceRepository.anonymousLogin().also {
            onLoginDone(it, initLD)
        } else if (!TextUtils.isEmpty(uuid) && !TextUtils.isEmpty(token)) AuthServiceRepository.login(uuid, token)
            .also {
                onLoginDone(it, initLD)
            } else {
            initLD.postValue(NEResult(NEEduHttpCode.BAD_REQUEST.code, false))
        }
        return initLD
    }

    private fun onLoginDone(result: LiveData<NEResult<NEEduLoginRes>>, initLD: MediatorLiveData<NEResult<Boolean>>) {
        result.observeForeverOnce { t ->
            val ok = t.success() && t.data != null
            if (ok) {
                afterLogin(t, initLD)
            } else {
                initLD.postValue(NEResult(t.code, t.requestId, t.msg, 0, false))
            }
        }
    }

    private fun afterLogin(t: NEResult<NEEduLoginRes>, initLD: MediatorLiveData<NEResult<Boolean>>) {
        eduLoginRes = t.data!!
        initRtcAndLoginIM(initLD)
        NEEduRetrofitManager.instance().addHeader("user", eduLoginRes.userUuid).addHeader("token", eduLoginRes.userToken)
    }

    private fun initRtcAndLoginIM(initLD: MediatorLiveData<NEResult<Boolean>>) {
        val mergeLD: MediatorLiveData<Boolean> = MediatorLiveData()
        var rtcServerAddresses: NERtcServerAddresses? = null // rtc private Server Addresses
        if (NEEduManager.eduOptions.useRtcAssetServerAddressConfig == true) {
            rtcServerAddresses = NEEduPrivatizationConfig.getRtcServerAddresses(NEEduManager.context)
        }
        val rtcLD = rtcManager.initEngine(
            NEEduManager.context,
            eduLoginRes.rtcKey,
            rtcServerAddresses
        )
        val imLoginLD = imManager.login(LoginInfo(eduLoginRes.userUuid, eduLoginRes.imToken, eduLoginRes.imKey))
        val onChanged = Observer<Boolean> {
            if (rtcLD.value == null || imLoginLD.value == null) {
                return@Observer
            }
            if (rtcLD.value == true && imLoginLD.value == true) {
                initInnerOthers()
                initLD.postValue(NEResult(NEEduHttpCode.SUCCESS.code, true))
            } else if (rtcLD.value == false) {
                initLD.postValue(NEResult(NEEduHttpCode.RTC_INIT_ERROR.code, false))
            } else {
                initLD.postValue(NEResult(NEEduHttpCode.IM_LOGIN_ERROR.code, false))
            }
        }
        mergeLD.addSource(rtcLD, onChanged)
        mergeLD.addSource(imLoginLD, onChanged)
        mergeLD.observeForeverOnce {}
    }

    private fun initInnerOthers() {
        cmdDispatcher = CMDDispatcher(this)
        neEduSync = NEEduSync(this)
        initService()
        handleError()
    }

    /**
     * Listen for IM errors & RTC errors
     */
    private fun handleError() {
        errorLD.addSource(imManager.errorLD) { t -> errorLD.postValue(t) }
        errorLD.addSource(rtcManager.errorLD) { t -> errorLD.postValue(t) }
        BaseRepository.errorLD.value = null// reset the last value
        errorLD.addSource(BaseRepository.errorLD) { t -> errorLD.postValue(t) }
        neEduSync?.let { errorLD.addSource(it.errorLD) { t -> errorLD.postValue(t) } }
    }

    /**
     * The live class not need the RTC functionality
     */
    override fun enterClass(neEduClassOptions: NEEduClassOptions): LiveData<NEResult<NEEduEntryRes>> {
        NEEduManager.classOptions = neEduClassOptions
        val enterLD = MediatorLiveData<NEResult<NEEduEntryRes>>()
        if (isLiveClass()) enterLiveClass(enterLD)
        else enterNormalClass(enterLD)
        return enterLD
    }

    override fun enterNormalClass(neEduClassOptions: NEEduClassOptions): LiveData<NEResult<NEEduEntryRes>> {
        NEEduManager.classOptions = neEduClassOptions
        val enterLD = MediatorLiveData<NEResult<NEEduEntryRes>>()
        neEduSync?.lastSequenceId = -1
        enterNormalClass(enterLD,true)
        return enterLD
    }

    private fun enterNormalClass(enterLD: MediatorLiveData<NEResult<NEEduEntryRes>>,isHasStreams:Boolean = false) {
        getRoomService().config(NEEduManager.classOptions).also {
            it.observeForeverOnce { t ->
                if (t.success() || t.success(NEEduHttpCode.CONFLICT.code)) {
                    t.data!!.apply { roomConfig = config }
                    realEnterClass(enterLD,isHasStreams)
                } else {
                    destroy()
                    enterLD.postValue(NEResult(t.code))
                }
            }
        }
    }

    private fun enterLiveClass(enterLD: MediatorLiveData<NEResult<NEEduEntryRes>>) {
        getRoomService().getConfig(NEEduManager.classOptions.classId).also {
            it.observeForeverOnce { t ->
                if (t.success()) {
                    t.data!!.apply { roomConfig = this }
                    if (roomConfig.isLiveClass()) { // Check the configuration
                        simulationEnterLiveClass(enterLD)
                    } else {
                        destroy()
                        enterLD.postValue(NEResult(NEEduHttpCode.ROOM_CONFIG_CONFLICT.code))
                    }
                } else {
                    destroy()
                    enterLD.postValue(NEResult(t.code))
                }
            }
        }
    }

    /**
     * Simulate the logic of joining a live class
     */
    private fun simulationEnterLiveClass(enterLD: MediatorLiveData<NEResult<NEEduEntryRes>>) {
        getRoomService().snapshot(NEEduManager.classOptions.classId).also { snap ->
            snap.observeForeverOnce { snapRes ->
                if (snapRes.success()) {
                    eduEntryRes = NEEduEntryRes(
                        member = NEEduEntryMember(
                            eduLoginRes.rtcKey,
                            NEEduManager.classOptions.roleType.value,
                            NEEduManager.classOptions.nickName,
                            eduLoginRes.userUuid
                        ),
                        room = snapRes.data!!.snapshot.room
                    )
                    observerAuth()
                    cmdDispatcher?.start()
                    enterLD.postValue(NEResult(NEEduHttpCode.SUCCESS.code, data = eduEntryRes))
                } else {
                    destroy()
                    enterLD.postValue(NEResult(NEEduHttpCode.ROOM_CONFIG_CONFLICT.code))
                }
            }
        }
    }

    override fun syncSnapshot() {
        neEduSync?.snapshot(NEEduManager.classOptions.classId)
    }

    private fun realEnterClass(enterLiveData: MediatorLiveData<NEResult<NEEduEntryRes>>,isHasStreams:Boolean = false) {
        getRoomService().entryClass(NEEduManager.classOptions,isHasStreams).also {
            it.observeForeverOnce { t ->
                if (t.success()) {
                    eduEntryRes = t.data!!
                    joinRtc()
                    NEEduForegroundService.start(
                        context = NEEduManager.context, NEEduManager.eduOptions
                            .foregroundServiceConfig ?: NEEduForegroundServiceConfig()
                    )
                    observerAuth()
                    IMManager.authLD.postValue(true)
                    cmdDispatcher?.start()
                    enterLiveData.postValue(t)
                } else {
                    destroy()
                    enterLiveData.postValue(t)
                }
            }
        }
    }

    private fun observerAuth() {
        imManager.authLD.observeForever(observer)
    }

    private fun joinRtc() {
        rtcManager.join(
            getEntryMember().rtcToken,
            getRoom().roomUuid,
            getEntryMember().rtcUid,
            // pushUrl设为null时不旁路推流
            if (getEntryMember().isHandsUp()) getRoom().pushUrl() else null
        )
    }

    override fun destroy() {
        imManager.authLD.removeObserver(observer)
        imManager.logout()
        NEEduForegroundService.cancel(context = NEEduManager.context)
        rtcManager.release()
        NEEduRtcVideoViewPool.clear()
        disposeService()
        cmdDispatcher?.destroy()
        ALog.i(TAG, "destroy")
        ALog.flush(true)
    }

    private fun initService() {
        roomService = NEEduRoomServiceImpl()
        memberService = NEEduMemberServiceImpl()
        imService = NEEduIMServiceImpl()
        rtcService = NEEduRtcServiceImpl()
        boardService = NEEduBoardServiceImpl()
        shareScreenService = NEEduShareScreenServiceImpl()
        handsUpServiceImpl = NEEduHandsUpServiceImpl()
        seatServiceImpl = NEEduSeatServiceImpl()
    }

    private fun disposeService() {
        if (this::boardService.isInitialized) boardService.dispose()
    }

    override fun getRoomService(): NEEduRoomService {
        return roomService
    }

    override fun getMemberService(): NEEduMemberService {
        return memberService
    }

    override fun getRtcService(): NEEduRtcService {
        return rtcService
    }

    override fun getIMService(): NEEduIMService {
        return imService
    }

    override fun getShareScreenService(): NEEduShareScreenService {
        return shareScreenService
    }

    override fun getBoardService(): NEEduBoardService {
        return boardService
    }

    override fun getHandsUpService(): NEEduHandsUpService {
        return handsUpServiceImpl
    }

    override fun getSeatService(): NEEduSeatService {
        return seatServiceImpl
    }

}
/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.impl

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.netease.nimlib.sdk.InvocationFuture
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.passthrough.model.PassthroughProxyData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.base.network.RetrofitManager
import com.netease.yunxin.app.wisdom.edu.logic.NEEduErrorCode
import com.netease.yunxin.app.wisdom.edu.logic.NEEduManager
import com.netease.yunxin.app.wisdom.edu.logic.cmd.CMDDispatcher
import com.netease.yunxin.app.wisdom.edu.logic.foreground.NEEduForegroundService
import com.netease.yunxin.app.wisdom.edu.logic.foreground.NEEduForegroundServiceConfig
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoomConfig
import com.netease.yunxin.app.wisdom.edu.logic.net.service.AuthServiceRepository
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduEntryMember
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduEntryRes
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduLoginRes
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduRoomConfigRes
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduClassOptions
import com.netease.yunxin.app.wisdom.edu.logic.service.*
import com.netease.yunxin.app.wisdom.edu.logic.service.impl.*
import com.netease.yunxin.app.wisdom.edu.logic.service.widget.NEEduRtcVideoViewPool
import com.netease.yunxin.app.wisdom.im.IMManager
import com.netease.yunxin.app.wisdom.rtc.RtcManager

/**
 * Created by hzsunyj on 4/21/21.
 */
internal object NEEduManagerImpl : NEEduManager {

    private const val TAG = "EduManagerImpl"

    override lateinit var eduLoginRes: NEEduLoginRes

    override lateinit var eduEntryRes: NEEduEntryRes

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

    var cmdDispatcher: CMDDispatcher? = null

    var neEduSync: NEEduSync? = null

    private lateinit var roomService: NEEduRoomServiceImpl

    private lateinit var memberService: NEEduMemberServiceImpl

    private lateinit var rtcService: NEEduRtcService

    private lateinit var boardService: NEEduBoardService

    private lateinit var shareScreenService: NEEduShareScreenService

    private lateinit var handsUpServiceImpl: NEEduHandsUpServiceImpl

    private lateinit var imService: NEEduIMService

    private val observer: Observer<Boolean> = Observer<Boolean> {
        it?.let { t ->
            if (t && neEduSync != null) {// t: true 表示已经重新登录，只要重新登录，需要重新同步一把数据
                syncSnapshot()
            }
        }
    }

    fun init(): LiveData<NEResult<Boolean>> {
        val initLD: MediatorLiveData<NEResult<Boolean>> = MediatorLiveData()
        AuthServiceRepository.anonymousLogin().also {
            it.observeForever(object : Observer<NEResult<NEEduLoginRes>> {
                override fun onChanged(t: NEResult<NEEduLoginRes>) {
                    it.removeObserver(this)
                    val ok = t.success() && t.data != null
                    if (ok) {
                        eduLoginRes = t.data!!
                        initRtc(initLD)
                        RetrofitManager.instance().addHeader("user", eduLoginRes.userUuid).addHeader(
                            "token", eduLoginRes.userToken
                        )
                    } else {
                        initLD.postValue(NEResult(t.code, t.requestId, t.msg, 0, false))
                    }
                }

            })
        }
        return initLD
    }

    private fun initRtc(initLD: MediatorLiveData<NEResult<Boolean>>) {
        rtcManager.initEngine(NEEduManager.context, eduLoginRes.rtcKey).also {
            it.observeForever(object : Observer<Boolean> {
                override fun onChanged(t: Boolean) {
                    it.removeObserver(this)
                    if (t) {
                        initInnerOthers()
                        initLD.postValue(NEResult(NEEduErrorCode.SUCCESS.code, true))
                    } else {
                        initLD.postValue(NEResult(NEEduErrorCode.RTC_INIT_ERROR.code, false))
                    }
                }

            })
        }
    }

    private fun initInnerOthers() {
        imManager.login(LoginInfo(eduLoginRes.userUuid, eduLoginRes.imToken, eduLoginRes.imKey))
        cmdDispatcher = CMDDispatcher(this)
        neEduSync = NEEduSync(this)
        initService()
        handleError()
    }

    /**
     * listen im error & rtc error
     */
    private fun handleError() {
        errorLD.addSource(imManager.errorLD) { t -> errorLD.postValue(t) }
        errorLD.addSource(rtcManager.errorLD) { t -> errorLD.postValue(t) }
    }


    override fun enterClass(neEduClassOptions: NEEduClassOptions): LiveData<NEResult<NEEduEntryRes>> {
        val enterLD = MediatorLiveData<NEResult<NEEduEntryRes>>()
        getRoomService().config(neEduClassOptions).also {
            it.observeForever(object : Observer<NEResult<NEEduRoomConfigRes>> {
                override fun onChanged(t: NEResult<NEEduRoomConfigRes>) {
                    it.removeObserver(this)
                    if (t.success() || t.success(NEEduErrorCode.CONFLICT.code)) {
                        roomConfig = t.data!!.config
                        realEnterClass(enterLD, neEduClassOptions)
                    } else {
                        enterLD.postValue(NEResult(t.code))
                    }
                }
            })
        }

        return enterLD
    }

    override fun syncSnapshot() {
        neEduSync?.snapshot(eduEntryRes.room.roomUuid)
    }

    private fun realEnterClass(
        enterLiveData: MediatorLiveData<NEResult<NEEduEntryRes>>,
        neEduClassOptions: NEEduClassOptions,
    ) {
        getRoomService().entryClass(neEduClassOptions).also {
            it.observeForever(object : Observer<NEResult<NEEduEntryRes>> {
                override fun onChanged(t: NEResult<NEEduEntryRes>) {
                    it.removeObserver(this)
                    if (t.success()) {
                        eduEntryRes = t.data!!
                        joinRtc()
                        NEEduForegroundService.start(context = NEEduManager.context, NEEduManager.eduOptions
                            .foregroundServiceConfig ?: NEEduForegroundServiceConfig())
                        observerAuth()
                        enterLiveData.postValue(t)
                    } else {
                        enterLiveData.postValue(t)
                    }
                }
            })
        }
    }

    private fun observerAuth() {
        imManager.authLD.observeForever(observer)
        cmdDispatcher?.start()
    }

    private fun joinRtc() {
        rtcManager.join(getEntryMember().rtcToken, eduEntryRes.room.roomUuid, getEntryMember().rtcUid)
    }

    fun httpProxy(data: PassthroughProxyData): InvocationFuture<PassthroughProxyData> {
        return imManager.passthroughService.httpProxy(data)
    }

    override fun destroy() {
        imManager.authLD.removeObserver(observer)
        NEEduForegroundService.cancel(context = NEEduManager.context)
        imManager.logout()
        rtcManager.release()
        cmdDispatcher?.destroy()
        NEEduRtcVideoViewPool.clear()
    }

    private fun initService() {
        roomService = NEEduRoomServiceImpl()
        memberService = NEEduMemberServiceImpl()
        rtcService = NEEduRtcServiceImpl()
        boardService = NEEduBoardServiceImpl()
        shareScreenService = NEEduShareScreenServiceImpl()
        handsUpServiceImpl = NEEduHandsUpServiceImpl()
        imService = NEEduIMServiceImpl()
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

}
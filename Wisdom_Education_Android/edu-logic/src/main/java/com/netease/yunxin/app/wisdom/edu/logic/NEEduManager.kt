/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.map
import com.netease.nimlib.sdk.util.NIMUtil
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.base.network.RetrofitManager
import com.netease.yunxin.app.wisdom.base.util.CryptoUtil
import com.netease.yunxin.app.wisdom.base.util.PreferenceUtil
import com.netease.yunxin.app.wisdom.base.util.observeForeverOnce
import com.netease.yunxin.app.wisdom.edu.logic.extras.NEEduClientType
import com.netease.yunxin.app.wisdom.edu.logic.extras.NEEduExtras
import com.netease.yunxin.app.wisdom.edu.logic.impl.NEEduManagerImpl
import com.netease.yunxin.app.wisdom.edu.logic.model.*
import com.netease.yunxin.app.wisdom.edu.logic.net.service.BaseRepository
import com.netease.yunxin.app.wisdom.edu.logic.net.service.BaseService
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduLoginRes
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduClassOptions
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduOptions
import com.netease.yunxin.app.wisdom.edu.logic.service.*
import com.netease.yunxin.app.wisdom.im.IMManager

/**
 * 提供各种业务服务
 */
interface NEEduManager {

    companion object {

        internal lateinit var context: Application

        internal lateinit var eduOptions: NEEduOptions

        internal lateinit var instance: NEEduManager

        internal lateinit var classOptions: NEEduClassOptions

        /**
         * sdk inner version code
         */
        private const val VERSION_CODE = 70


        /**
         * @suppress
         *
         * @param context
         * @param eduOptions
         */
        fun config(context: Application, eduOptions: NEEduOptions) {
            this.context = context
            this.eduOptions = eduOptions
            BaseRepository.appKey = eduOptions.appKey
            BaseService.baseUrl = eduOptions.baseUrl
            IMManager.config(context, eduOptions.appKey, eduOptions.reuseIM ?: false)
            if (NIMUtil.isMainProcess(context)) {
                NEEduActivityManger.init(context)
                PreferenceUtil.init(context)
                RetrofitManager.instance()
                    .addHeader(NEEduExtras.AUTHORIZATION, CryptoUtil.getAuth(eduOptions.authorization))
                    .addHeader(NEEduExtras.DEVICE_ID, PreferenceUtil.deviceId)
                    .addHeader(NEEduExtras.CLIENT_TYPE, NEEduClientType.ANDROID.type)
                    .addHeader(NEEduExtras.VERSION_CODE, VERSION_CODE.toString())
            }
        }

        /**
         * @suppress
         *
         * @param uuid
         * @param token
         * @return
         */
        fun init(uuid: String, token: String): LiveData<NEResult<NEEduManager>> {
            val managerLD: MediatorLiveData<NEResult<NEEduManager>> = MediatorLiveData<NEResult<NEEduManager>>()
            NEEduManagerImpl.init(uuid, token).observeForeverOnce { t ->
                if (t.success()) {
                    instance = NEEduManagerImpl
                    managerLD.postValue(NEResult(t.code, NEEduManagerImpl))
                } else {
                    NEEduManagerImpl.destroy()
                    managerLD.postValue(NEResult(t.code))
                }
            }
            return managerLD
        }

    }

    /**
     * the information data returned by the login interface
     */
    var eduLoginRes: NEEduLoginRes

    /**
     * room config
     */
    var roomConfig: NEEduRoomConfig

    /**
     * LiveData which observes error events
     */
    val errorLD: MediatorLiveData<Int>

    /**
     * Whether the member userUuid is mine
     *
     * @param userUuid member userUuid
     * @return Whether the member userUuid is mine
     */
    fun isSelf(userUuid: String): Boolean

    /**
     * streams和properties都是不可靠的
     */
    fun getEntryMember(): NEEduEntryMember

    /**
     * destroy instance of NEEduManager
     *
     */
    fun destroy()

    /**课堂管理*/
    fun getRoomService(): NEEduRoomService

    /**用户列表*/
    fun getMemberService(): NEEduMemberService

    /**音视频*/
    fun getRtcService(): NEEduRtcService

    /**消息聊天*/
    fun getIMService(): NEEduIMService

    /**屏幕分享*/
    fun getShareScreenService(): NEEduShareScreenService

    /**白板通用控制*/
    fun getBoardService(): NEEduBoardService

    /**举手上台*/
    fun getHandsUpService(): NEEduHandsUpService

    /**
     * 进入教室
     */
    fun enterClass(neEduClassOptions: NEEduClassOptions): LiveData<NEResult<NEEduEntryRes>>

    /**
     * 同步快照
     */
    fun syncSnapshot()

    /**
     * Whiteboard auth
     *
     * @return Whiteboard auth
     */
    fun getWbAuth(): NEEduWbAuth?

    /**
     * Get current room
     *
     * @return current room
     */
    fun getRoom(): NEEduRoom

    /**
     * Whether I am the host
     *
     * @return Whether I am the host
     */
    fun isHost(): Boolean

    /**
     * Whether current room is live class
     *
     * @return Whether current room is live class
     */
    fun isLiveClass(): Boolean {
        return classOptions.sceneType == NEEduSceneType.LIVE_SIMPLE
    }

}
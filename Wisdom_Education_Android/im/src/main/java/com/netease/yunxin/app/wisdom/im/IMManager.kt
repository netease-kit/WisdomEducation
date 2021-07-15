/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.im

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import com.netease.nimlib.sdk.*
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.AuthServiceObserver
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.chatroom.ChatRoomService
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver
import com.netease.nimlib.sdk.passthrough.PassthroughService
import com.netease.nimlib.sdk.passthrough.PassthroughServiceObserve
import com.netease.nimlib.sdk.passthrough.model.PassthroughNotifyData
import com.netease.nimlib.sdk.passthrough.model.PassthroughProxyData
import com.netease.nimlib.sdk.util.NIMUtil
import com.netease.yunxin.kit.alog.ALog

/**
 * Created by hzsunyj on 4/21/21.
 */
object IMManager {

    private const val TAG = "IMManager"

    private var reuseIM: Boolean = false

    private lateinit var authService: AuthService

    private lateinit var authServiceObserver: AuthServiceObserver

    private lateinit var passthroughServiceObserve: PassthroughServiceObserve

    lateinit var passthroughService: PassthroughService

    lateinit var chatRoomService: ChatRoomService

    lateinit var chatRoomServiceObserver: ChatRoomServiceObserver

    val errorLD: MediatorLiveData<Int> = MediatorLiveData()

    val authLD: MediatorLiveData<Boolean> = MediatorLiveData()

    val passthroughLD: MediatorLiveData<String> = MediatorLiveData()

    private var loginInfo: LoginInfo? = null

    //dispatch message
    private val passthrougthObserver = Observer<PassthroughNotifyData> { t ->
        t?.body?.let {
            // post value may be lost message
            passthroughLD.value = t.body
        }
    }

    /// other can auto login
    private val onlineObserver = Observer<StatusCode>
    { t ->
        ALog.i(TAG, "online status change $t")
        when (t) {
            StatusCode.KICKOUT, StatusCode.KICK_BY_OTHER_CLIENT, StatusCode.FORBIDDEN, StatusCode.PWD_ERROR -> {
                authLD.postValue(false)
                errorLD.postValue(IMErrorCode.mapError(t.value))
                loginInfo = null
            }
            StatusCode.LOGINED -> {
                authLD.postValue(true)
            }
            StatusCode.NET_BROKEN, StatusCode.UNLOGIN -> {
                authLD.postValue(false)
            }
        }
    }

    fun config(context: Context, appKey: String, reuse: Boolean) {
        this.reuseIM = reuse
        if (!this.reuseIM) {
            val value = SDKOptions()
            value.appKey = appKey
            value.disableAwake = true
            NIMClient.config(context, null, value)
            if (NIMUtil.isMainProcess(context)) {
                NIMClient.initSDK()
            }
        }
        if (NIMUtil.isMainProcess(context)) {
            initService()
        }
    }

    private fun initService() {
        authService = NIMClient.getService(AuthService::class.java)
        authServiceObserver = NIMClient.getService(AuthServiceObserver::class.java)
        passthroughService = NIMClient.getService(PassthroughService::class.java)
        passthroughServiceObserve = NIMClient.getService(PassthroughServiceObserve::class.java)
        chatRoomService = NIMClient.getService(ChatRoomService::class.java)
        chatRoomServiceObserver = NIMClient.getService(ChatRoomServiceObserver::class.java)
    }

    fun login(loginInfo: LoginInfo) {
        this.loginInfo = loginInfo
        if (!reuseIM) {
            authService.login(loginInfo).setCallback(object : RequestCallback<LoginInfo> {
                override fun onSuccess(param: LoginInfo) {
                    authLD.postValue(true)
                }

                override fun onFailed(code: Int) {
                    ALog.i(TAG, "login failed $code")
                    authLD.postValue(false)
                }

                override fun onException(exception: Throwable?) {
                    ALog.i(TAG, "login exception ${exception?.message}")
                    authLD.postValue(false)
                }
            })
        }
        authServiceObserver.observeOnlineStatus(onlineObserver, true)
        passthroughServiceObserve.observePassthroughNotify(passthrougthObserver, true)
    }

    /**
     * may be check login states
     */
    fun httpProxy(data: PassthroughProxyData): InvocationFuture<PassthroughProxyData> {
        return passthroughService.httpProxy(data)
    }

    fun logout() {
        if (this::authServiceObserver.isInitialized) {
            authServiceObserver.observeOnlineStatus(onlineObserver, false)
        }
        if (this::passthroughServiceObserve.isInitialized) {
            passthroughServiceObserve.observePassthroughNotify(passthrougthObserver, false)
        }
        if (!reuseIM) {
            /// ignore return value
            authService.logout()
        }
        errorLD.postValue(null)
        passthroughLD.postValue(null)
        authLD.postValue(null)
    }
}
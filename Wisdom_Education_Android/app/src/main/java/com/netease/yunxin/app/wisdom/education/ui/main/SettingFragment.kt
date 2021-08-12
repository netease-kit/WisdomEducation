/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.education.ui.main

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.Fragment
import com.netease.nimlib.sdk.NIMClient
import com.netease.nimlib.sdk.Observer
import com.netease.nimlib.sdk.RequestCallback
import com.netease.nimlib.sdk.StatusCode
import com.netease.nimlib.sdk.auth.AuthService
import com.netease.nimlib.sdk.auth.AuthServiceObserver
import com.netease.nimlib.sdk.auth.LoginInfo
import com.netease.nimlib.sdk.msg.MessageBuilder
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum
import com.netease.yunxin.app.wisdom.base.util.PreferenceUtil
import com.netease.yunxin.app.wisdom.edu.ui.viewbinding.viewBinding
import com.netease.yunxin.app.wisdom.education.BuildConfig
import com.netease.yunxin.app.wisdom.education.R
import com.netease.yunxin.app.wisdom.education.databinding.SettingFragmentBinding
import com.netease.yunxin.app.wisdom.education.ui.MainActivity
import com.netease.yunxin.kit.alog.ALog

class SettingFragment : Fragment(R.layout.setting_fragment), Observer<StatusCode> {
    private val binding: SettingFragmentBinding by viewBinding()

    companion object {
        fun newInstance() = SettingFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toggleIMReuse.apply {
            isChecked = PreferenceUtil.reuseIM
            setOnCheckedChangeListener { _, isChecked ->
                PreferenceUtil.reuseIM = isChecked
            }
        }
        refreshOnlineStatus()
        binding.loginBtn.setOnClickListener { login() }
        binding.logoutBtn.setOnClickListener { logout() }
        binding.tvBack.setOnClickListener {
            (activity as MainActivity).hideSettingFragment()
        }
    }

    private fun login() {
        val account = binding.firstEditor.text.toString()
        val token: String = binding.secondEditor.text.toString()
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            NIMClient.getService(AuthService::class.java).login(LoginInfo(account, token, BuildConfig.APP_KEY))
                .setCallback(object :
                    RequestCallback<LoginInfo> {
                    override fun onSuccess(param: LoginInfo) {
                        ALog.i("login success $param")
                        refreshOnlineStatus()
                    }

                    override fun onFailed(code: Int) {
                        ALog.i("login failed $code")
                    }

                    override fun onException(exception: Throwable?) {
                        ALog.i("login exception ${exception?.message}")
                    }
                })
        }
    }

    private fun logout() {
        NIMClient.getService(AuthService::class.java).logout()
        //延迟刷新
        binding.firstEditor.postDelayed({ refreshOnlineStatus() }, 2000)
    }

    override fun onStart() {
        super.onStart()
        try {
            NIMClient.getService(AuthServiceObserver::class.java).observeOnlineStatus(this, true)
        } catch (e: Exception) {
            ALog.e(e.message)
        }
    }

    override fun onStop() {
        super.onStop()
        try {
            NIMClient.getService(AuthServiceObserver::class.java).observeOnlineStatus(this, false)
        } catch (e: Exception) {
            ALog.e(e.message)
        }
    }

    override fun onEvent(statusCode: StatusCode) {
        ALog.i("onIMEvent: $statusCode")
        refreshOnlineStatus()
    }

    private fun refreshOnlineStatus() {
        val fromAccount = MessageBuilder.createTextMessage("", SessionTypeEnum.None, "").fromAccount
        binding.imLoginState.text = "当前IM状态: $fromAccount#${NIMClient.getStatus()}."
    }
}
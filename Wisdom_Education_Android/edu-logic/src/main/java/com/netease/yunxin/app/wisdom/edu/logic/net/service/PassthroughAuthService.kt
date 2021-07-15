/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduLoginRes

/**
 * Created by hzsunyj on 2021/5/26.
 */
object PassthroughAuthService : AuthService, BaseService {

    private val authService = getService(AuthService::class.java)

    override fun login(appKey: String, userUuid: String): LiveData<NEResult<NEEduLoginRes>> {
        return authService.login(appKey, userUuid)
    }

    override fun anonymousLogin(appKey: String): LiveData<NEResult<NEEduLoginRes>> {
        return authService.anonymousLogin(appKey)
    }
}
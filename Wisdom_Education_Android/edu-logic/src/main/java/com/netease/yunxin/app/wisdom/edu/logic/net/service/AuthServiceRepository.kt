/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduLoginRes

/**
 */
object AuthServiceRepository : BaseRepository() {

    private val authService = getDelegateService(AuthService::class.java)

    fun login(userUuid: String): LiveData<NEResult<NEEduLoginRes>> {
        return authService.login(appKey, userUuid)
    }

    fun anonymousLogin(): LiveData<NEResult<NEEduLoginRes>> {
        return authService.anonymousLogin(appKey)
    }
}
/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service

import androidx.lifecycle.LiveData
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.base.network.RetrofitManager
import com.netease.yunxin.app.wisdom.edu.logic.BuildConfig
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduLoginRes
import retrofit2.http.*

/**

 */
internal interface AuthService {
    @POST("/scene/apps/{appKey}/v1/users/{userUuid}/login")
    fun login(
        @Path("appKey") appKey: String,
        @Path("userUuid") userUuid: String,
    ): LiveData<NEResult<NEEduLoginRes>>

    @POST("/scene/apps/{appKey}/v1/anonymous/login")
    fun anonymousLogin(
        @Path("appKey") appKey: String,
    ): LiveData<NEResult<NEEduLoginRes>>
}

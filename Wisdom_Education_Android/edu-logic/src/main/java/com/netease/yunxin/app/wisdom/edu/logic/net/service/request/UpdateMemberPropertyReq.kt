/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.net.service.request

import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduStateValue

data class NEEduUpdateMemberPropertyReq(
    val value: Int? = NEEduStateValue.OPEN,
    val audio: Int? = null,
    val video: Int? = null,
    val drawable: Int? = null, // Whiteboard
)

enum class NEEduMemberPropertiesType(val type: String) {
    SCREENSHARE("screenShare"),
    STREAMAV("streamAV"),
    WHITEBOARD("whiteboard"),
    AVHANDSUP("avHandsUp"),
}
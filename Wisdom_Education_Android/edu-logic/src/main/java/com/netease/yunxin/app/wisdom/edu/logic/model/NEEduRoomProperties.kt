/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

data class NEEduRoomProperties(
    var screenShare: NEEduScreenShareProperty?,
    var whiteboard: NEEduWhiteboardProperty?,
    var streamAV: NEEduStreamAVProperty?,
    var avHandsUp: NEEduAvHandsUpProperty?,
) {
    fun merge(properties: NEEduMemberProperties): NEEduMemberProperties {
        if (properties.screenShare != null) {
            screenShare = properties.screenShare
        }
        if (properties.whiteboard != null) {
            whiteboard = properties.whiteboard
        }
        if (properties.streamAV != null) {
            streamAV = properties.streamAV
        }
        if (properties.avHandsUp != null) {
            avHandsUp = properties.avHandsUp
        }
        return properties
    }
}

/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

import java.util.*

data class NEEduRoomConfig(
    val permissions: NEEduPermissions?,
    val roleConfigs: NEEduRoleConfigs?,
    val sceneType: String,
) {
    fun memberStreamsPermission(): NEEduPermissionStreams? {
        return permissions?.member?.streams
    }

    fun roomStatesPermission(): NEEduRoomPermissionStates? {
        return permissions?.room?.states
    }

    fun memberPropertiesPermission(): NEEduProperties? {
        return permissions?.member?.properties
    }

    fun is1V1(): Boolean {
        return sceneType == NEEduSceneType.ONE_TO_ONE.value
    }

    fun isBig(): Boolean {
        return sceneType == NEEduSceneType.BIG.value
    }

    fun isSmall(): Boolean {
        return sceneType == NEEduSceneType.SMALL.value
    }
}

data class NEEduPermissions(
    val room: NEEduRoomPermissions,
    val member: NEEduMemberPermissions,
)

data class NEEduRoleConfigs(
    val broadcaster: NEEduRoleLimit,
    val host: NEEduRoleLimit,
)

data class NEEduMemberPermissions(
    val properties: NEEduProperties,
    val streams: NEEduPermissionStreams,
)

data class NEEduRoomPermissions(
    val properties: MutableMap<String, NEEduRoomPermissionsProperty>,
    val states: NEEduRoomPermissionStates,
)

data class NEEduProperties(
    val avHandsUp: NEEduPermissionRoleList?,
    val screenShare: NEEduPermissionRoleList?,
    val whiteboard: NEEduPermissionRoleList?,
)

data class NEEduPermissionStreams(
    val audio: NEEduPermissionRoleList?,
    val video: NEEduPermissionRoleList?,
    val subVideo: NEEduPermissionRoleList?,
)

data class NEEduRoomPermissionStates(
    val pause: NEEduPermissionRoleList,
    val step: NEEduPermissionRoleList,
)

data class NEEduRoomPermissionsProperty(
    val exclusive: Boolean?,
    val memberGrant: NEEduMemberGrant?,
    val roles: List<String>,
)

data class NEEduMemberGrant(
    val screenShare: NEEduScreenSharePermission,
)

data class NEEduScreenSharePermission(
    val value: Int,
)

data class NEEduPermissionRoleList(
    val roles: List<String>,
) {
    fun hasAllPermission(role: String): Boolean {
        return roles.contains(role.lowercase(Locale.getDefault()))
    }

    private fun hasSelfPermission(role: String): Boolean {
        var selfRole = "${role}.self"
        return roles.contains(selfRole.lowercase(Locale.getDefault()))
    }

    fun hasPermission(role: String): Boolean {
        return hasAllPermission(role) || hasSelfPermission(role)
    }
}

data class NEEduRoleLimit(
    val limit: Int,
)
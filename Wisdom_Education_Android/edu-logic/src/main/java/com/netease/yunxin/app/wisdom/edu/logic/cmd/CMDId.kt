/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.cmd

/**
 * 
 */
object CMDId {
    // Room state
    /**
     * Room state change
     */
    const val ROOM_STATES_CHANGE = 1

    /**
     * Delete room state
     */
    const val ROOM_STATES_DELETE = 2

    // Room properties
    /**
     * Room properties change
     */
    const val ROOM_PROPERTIES_CHANGE = 10

    /**
     * Delete room properties
     */
    const val ROOM_PROPERTIES_DELETE = 11


    // Room member properties
    /**
     * Room member properties change
     */
    const val ROOM_MEMBER_PROPERTIES_CHANGE = 20

    /**
     * Delete room member properties
     */
    const val ROOM_MEMBER_PROPERTIES_DELETE = 21

    // Join or leave the room
    /**
     * Join the room
     */
    const val USER_JOIN = 30

    /**
     * Leave the room
     */
    const val USER_LEAVE = 31

    // Member stream
    /**
     * Room members stream change
     */
    const val STREAM_CHANGE = 40

    /**
     * Remove members streams
     */
    const val STREAM_REMOVE = 41

}
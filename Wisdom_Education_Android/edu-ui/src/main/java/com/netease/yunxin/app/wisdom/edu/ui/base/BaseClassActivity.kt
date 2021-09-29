/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.edu.ui.base

import androidx.appcompat.app.AppCompatActivity
import com.netease.yunxin.app.wisdom.edu.logic.NEEduManager
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduEntryMember
import com.netease.yunxin.app.wisdom.edu.logic.model.NEEduRoom

abstract class BaseClassActivity(layoutId: Int) : AppCompatActivity(layoutId) {
    open lateinit var eduManager: NEEduManager
    open lateinit var eduRoom: NEEduRoom
    open lateinit var entryMember: NEEduEntryMember
}
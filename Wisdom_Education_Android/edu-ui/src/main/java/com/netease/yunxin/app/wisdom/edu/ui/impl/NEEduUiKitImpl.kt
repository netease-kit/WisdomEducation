/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.impl

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.netease.yunxin.app.wisdom.base.network.NEResult
import com.netease.yunxin.app.wisdom.edu.logic.NEEduManager
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduLoginRes
import com.netease.yunxin.app.wisdom.edu.logic.net.service.response.NEEduEntryRes
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduClassOptions
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduRoleType
import com.netease.yunxin.app.wisdom.edu.logic.options.NEEduSceneType
import com.netease.yunxin.app.wisdom.edu.ui.NEEduUiKit
import com.netease.yunxin.app.wisdom.edu.ui.clazz.*

/**
 * Created by hzsunyj on 4/21/21.
 */
internal class NEEduUiKitImpl : NEEduUiKit {

    private lateinit var eduManager: NEEduManager

    override var neEduLoginRes: NEEduLoginRes? = null
        get() = neEduManager?.eduLoginRes

    override var neEduManager: NEEduManager? = null
        get() = eduManager


    fun init(): LiveData<NEResult<NEEduManager>> {
        return NEEduManager.init().map {
            if(it.data != null) {
                eduManager = it.data!!
            }
            it
        }
    }

    override fun enterClass(context: Context, neEduClassOptions: NEEduClassOptions): LiveData<NEResult<NEEduEntryRes>> {
        return eduManager.enterClass(neEduClassOptions).map {
            if (it.success()) {
                if (neEduClassOptions.roleType == NEEduRoleType.HOST) {
                    when (neEduClassOptions.sceneType) {
                        NEEduSceneType.ONE_TO_ONE -> {
                            OneToOneTeacherActivity.start(context)
                        }
                        NEEduSceneType.SMALL -> {
                            SmallClazzTeacherActivity.start(context)
                        }
                        NEEduSceneType.BIG -> {
                            BigClazzTeacherActivity.start(context)
                        }
                    }
                } else {
                    when (neEduClassOptions.sceneType) {
                        NEEduSceneType.ONE_TO_ONE -> {
                            OneToOneStudentActivity.start(context)
                        }
                        NEEduSceneType.SMALL -> {
                            SmallClazzStudentActivity.start(context)
                        }
                        NEEduSceneType.BIG -> {
                            BigClazzStudentActivity.start(context)
                        }
                    }
                }
            }
            it
        }
    }

    override fun destroy() {
        eduManager.destroy()
    }
}
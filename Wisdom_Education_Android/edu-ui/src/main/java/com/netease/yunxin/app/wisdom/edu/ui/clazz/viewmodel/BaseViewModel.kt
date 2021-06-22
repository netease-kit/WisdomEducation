/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.viewmodel

import androidx.lifecycle.ViewModel
import com.netease.yunxin.app.wisdom.edu.logic.NEEduManager
import com.netease.yunxin.app.wisdom.edu.ui.NEEduUiKit

/**
 * Created by hzsunyj on 2021/6/9.
 */
open class BaseViewModel : ViewModel() {
    open val eduManager: NEEduManager = NEEduUiKit.instance!!.neEduManager!!
}
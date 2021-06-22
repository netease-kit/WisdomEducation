/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.base.util

import android.graphics.Color
import android.os.Build
import android.view.*

object StatusBarUtil {
    fun hideStatusBar(window: Window?, darkText: Boolean) {
        if (window == null) return
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        var flag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && darkText) {
            flag = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.decorView.systemUiVisibility = flag or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }


    fun toggleStatusBar(window: Window?, hide: Boolean = true) {
        toggleStatusBar(window, hide, StatusMode.NORMAL)
    }

    fun toggleImmersiveStatusBar(window: Window?, hide: Boolean = true) {
        toggleStatusBar(window, hide, StatusMode.IMMERSIVE)
    }

    fun toggleStickImmersiveStatusBar(window: Window?, hide: Boolean = true) {
        toggleStatusBar(window, hide, StatusMode.STICKY)
    }

    private fun toggleStatusBar(window: Window?, hide: Boolean = true, statusMode: StatusMode = StatusMode.NORMAL) {
        window?.let {
            var flag: Int = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View
                .SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            when (statusMode) {
                StatusMode.IMMERSIVE -> if (hide) flag or View.SYSTEM_UI_FLAG_IMMERSIVE else flag
                StatusMode.STICKY -> if (hide) flag or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY else flag
                else -> flag
            }
            window.decorView.systemUiVisibility = if (hide) flag or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View
                .SYSTEM_UI_FLAG_FULLSCREEN else flag
        }
    }

    enum class StatusMode {
        NORMAL,
        IMMERSIVE,
        STICKY
    }
}
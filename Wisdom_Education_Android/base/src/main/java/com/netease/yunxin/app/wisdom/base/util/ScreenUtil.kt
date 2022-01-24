/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */
package com.netease.yunxin.app.wisdom.base.util

import android.content.Context

object ScreenUtil {
    private const val TAG = "Demo.ScreenUtil"
    private const val RATIO = 0.85
    var screenWidth = 0
    var screenHeight = 0
    var screenMin // The height of the aspect ratio
            = 0
    var screenMax // The width of the aspect ratio
            = 0
    var density = 0f
    var scaleDensity = 0f
    var xdpi = 0f
    var ydpi = 0f
    var densityDpi = 0
    var statusbarheight = 0
    var navbarheight = 0
    fun dip2px(dipValue: Float): Int {
        return (dipValue * density + 0.5f).toInt()
    }

    fun px2dip(pxValue: Float): Int {
        return (pxValue / density + 0.5f).toInt()
    }

    fun sp2px(spValue: Float): Int {
        return (spValue * scaleDensity + 0.5f).toInt()
    }

    fun init(context: Context?) {
        if (null == context) {
            return
        }
        val dm = context.applicationContext.resources.displayMetrics
        screenWidth = dm.widthPixels
        screenHeight = dm.heightPixels
        screenMin = if (screenWidth > screenHeight) screenHeight else screenWidth
        density = dm.density
        scaleDensity = dm.scaledDensity
        xdpi = dm.xdpi
        ydpi = dm.ydpi
        densityDpi = dm.densityDpi
    }

    fun getInfo(context: Context?) {
        if (null == context) {
            return
        }
        val dm = context.applicationContext.resources.displayMetrics
        screenWidth = dm.widthPixels
        screenHeight = dm.heightPixels
        screenMin = if (screenWidth > screenHeight) screenHeight else screenWidth
        screenMax = if (screenWidth < screenHeight) screenHeight else screenWidth
        density = dm.density
        scaleDensity = dm.scaledDensity
        xdpi = dm.xdpi
        ydpi = dm.ydpi
        densityDpi = dm.densityDpi
        statusbarheight = getStatusBarHeight(context)
        navbarheight = getNavBarHeight(context)
    }

    fun getStatusBarHeight(context: Context): Int {
        if (statusbarheight == 0) {
            try {
                val c = Class.forName("com.android.internal.R\$dimen")
                val o = c.newInstance()
                val field = c.getField("status_bar_height")
                val x = field[o] as Int
                statusbarheight = context.resources.getDimensionPixelSize(x)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (statusbarheight == 0) {
            statusbarheight = dip2px(25f)
        }
        return statusbarheight
    }

    fun getNavBarHeight(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }
}
/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.foreground

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import androidx.core.app.NotificationCompat

/**
 * Created by hzsunyj on 2021/5/27.
 */
object NEEduNotificationManager {

    var manager: NotificationManager? = null

    private fun getManager(context: Context): NotificationManager {
        if (manager == null) {
            manager = context.applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return manager!!
    }

    fun createForegroundNotificationChannel(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val config: NEEduForegroundServiceConfig = NEEduForegroundService.getForegroundServiceConfig() ?: return
            val name: CharSequence = config.channelName
            val description = config.channelDesc
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(config.channelId, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            getManager(context).createNotificationChannel(channel)
        }
    }

    fun buildForegroundNotification(context: Context): Notification? {
        val config: NEEduForegroundServiceConfig = NEEduForegroundService.getForegroundServiceConfig() ?: return null
        val notificationIntent = Intent()
        if (TextUtils.isEmpty(config.launchActivityName)) {
            val forPackage = context.packageManager.getLaunchIntentForPackage(context.packageName)
            config.launchActivityName = if (forPackage != null) forPackage.component!!.className else null
        }
        notificationIntent.component = ComponentName(context, config.launchActivityName!!)
        val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0)
        return NotificationCompat.Builder(context, config.channelId).setContentTitle(
            config.contentTitle).setContentText(config.contentText).setSmallIcon(
            if (config.smallIcon == 0) context.applicationInfo.icon else config.smallIcon).setContentIntent(
            pendingIntent).setTicker(config.ticker).build()
    }

    fun cancelNotification(context: Context, notificationId: Int) {
        getManager(context).cancel(notificationId)
    }
}
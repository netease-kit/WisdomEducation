/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.foreground

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.os.Process
import com.netease.yunxin.app.wisdom.edu.logic.NEEduManager
import com.netease.yunxin.kit.alog.ALog
import kotlin.system.exitProcess

/**
 * 
 */
class NEEduForegroundService : Service() {

    companion object {
        private const val ONGOING_NOTIFICATION_ID = 0x9527

        private var foregroundServiceConfig: NEEduForegroundServiceConfig? = null

        private val captureIntent: Intent by lazy {
            val mediaProjectionManager = NEEduManager.context.getSystemService(
                MEDIA_PROJECTION_SERVICE
            ) as MediaProjectionManager
            mediaProjectionManager.createScreenCaptureIntent()
        }

        /**
         * Screen sharing intent used to request screen sharing
         */
        val neCaptureIntent: Intent
            get() = captureIntent

        fun start(context: Context, config: NEEduForegroundServiceConfig) {
            foregroundServiceConfig = config
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, NEEduForegroundService::class.java))
            } else {
                context.startService(Intent(context, NEEduForegroundService::class.java))
            }
        }

        fun cancel(context: Context) {
            // stopService may throw remoteException.
            //
            // if the service had been started as foreground, but
            // being brought down before actually showing a notification.
            // That is not allowed.
            try {
                context.stopService(Intent(context, NEEduForegroundService::class.java))
                NEEduNotificationManager.cancelNotification(context, ONGOING_NOTIFICATION_ID)
            } catch (e: Exception) {
                ALog.e("NEEduForegroundService", "cancel foreground service error", e)
            }
        }

        fun getForegroundServiceConfig(): NEEduForegroundServiceConfig? {
            return foregroundServiceConfig
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        onStartForeground()
        return super.onStartCommand(intent, flags, startId)
    }


    private fun onStartForeground() {
        NEEduNotificationManager.createForegroundNotificationChannel(this)
        // Notification ID cannot be 0.
        val notification: Notification = NEEduNotificationManager.buildForegroundNotification(this) ?: return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(ONGOING_NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
        } else {
            startForeground(ONGOING_NOTIFICATION_ID, notification)
        }
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ALog.i("NEForegroundService", "kill self due to task removed!")
            //quit application
            stopSelf()
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
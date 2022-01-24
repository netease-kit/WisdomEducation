/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * 
 */
class NEEduActivityManger {

    companion object {
        val livingActivities: MutableList<Activity> = mutableListOf()

        var runningActivity: Activity? = null

        fun init(app: Application) {
            registerActivityLifeCycle(app)
        }

        private fun registerActivityLifeCycle(app: Application) {
            app.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
                override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                    livingActivities.add(activity)
                }

                override fun onActivityStarted(activity: Activity) {

                }

                override fun onActivityResumed(activity: Activity) {
                    runningActivity = activity
                }

                override fun onActivityPaused(activity: Activity) {
                    runningActivity = null
                }

                override fun onActivityStopped(activity: Activity) {
                }

                override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                }

                override fun onActivityDestroyed(activity: Activity) {
                    livingActivities.remove(activity)
                }

            })
        }
    }

}
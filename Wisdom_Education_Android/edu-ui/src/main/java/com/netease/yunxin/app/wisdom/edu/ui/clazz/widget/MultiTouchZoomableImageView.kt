/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */
package com.netease.yunxin.app.wisdom.edu.ui.clazz.widget

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import com.netease.yunxin.kit.alog.ALog
import kotlin.math.abs

/*
* Copyright 2012 Laurence Dawson
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/*
 * This class is based upon the file ImageViewTouchBase.java which can be found at:
 * https://dl-ssl.google.com/dl/googlesource/git-repo/repo
 *  
 * Copyright (C) 2009 The Android Open Source Project
 */
open class MultiTouchZoomableImageView : BaseZoomableImageView {
    // Scale and gesture listeners for the view
    private var mGestureDetector: GestureDetector? = null
    private var mScaleDetector: ScaleGestureDetector? = null
    protected var transIgnoreScale = false
    private var scaleRecognized = false

    // Programatic entry point
    constructor(context: Context?) : super(context!!) {
        initMultiTouchZoomableImageView(context)
    }

    // XML entry point
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        initMultiTouchZoomableImageView(context)
    }

    // Setup the view
    protected fun initMultiTouchZoomableImageView(context: Context?) {
        // Setup the gesture and scale listeners
        mScaleDetector = ScaleGestureDetector(context, ScaleListener())
        mGestureDetector = GestureDetector(context, MyGestureListener())
    }

    // Adjusts the zoom of the view
    internal inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            // Check if the detector is in progress in order to proceed
            if (detector != null && detector.isInProgress) {
                try {
                    // Grab the scale
                    var targetScale = getScale() * detector.scaleFactor
                    // Correct for the min scale
                    targetScale = maxZoom().coerceAtMost(targetScale.coerceAtLeast(1.0f))

                    // Zoom and invalidate the view
                    zoomTo(targetScale, detector.focusX, detector.focusY)
                    invalidate()
                    scaleRecognized = true
                    return true
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
            return false
        }
    }

    // Handles taps and scrolls of the view
    private inner class MyGestureListener : SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (mImageGestureListener != null) {
                mImageGestureListener!!.onImageGestureSingleTapConfirmed()
                return false
            }
            return super.onSingleTapConfirmed(e)
        }

        override fun onLongPress(e: MotionEvent) {
            if (mImageGestureListener != null && !scaleRecognized) {
                mImageGestureListener!!.onImageGestureLongPress()
            }
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            try {
                // Skip if there are multiple points of contact
                if (e1 != null && e1.pointerCount > 1 || e2 != null && e2.pointerCount > 1 || mScaleDetector != null && mScaleDetector!!.isInProgress) return false

                // Scroll the bitmap
                if (transIgnoreScale || getScale() > zoomDefault()) {
                    stopFling()
                    postTranslate(-distanceX, -distanceY)
                    if (isScrollOver(distanceX)) {
                        if (mViewPager != null) {
                            mViewPager!!.requestDisallowInterceptTouchEvent(false)
                        }
                    } else {
                        if (mViewPager != null) {
                            mViewPager!!.requestDisallowInterceptTouchEvent(true)
                        }
                    }
                    center(vertical = true, horizontal = true, animate = false)
                } else {
                    if (mViewPager != null) {
                        mViewPager!!.requestDisallowInterceptTouchEvent(false)
                    }
                }
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }

            // Default case
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            // If the zoom is over 1x, reset to 1x
            if (getScale() != zoomDefault()) {
                zoomTo(zoomDefault())
            } else zoomTo(zoomDefault() * 3, e.x, e.y, 200f)

            // Always true as double tap was performed
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (e1 != null && e1.pointerCount > 1 || e2 != null && e2.pointerCount > 1) return false
            if (mScaleDetector!!.isInProgress) return false
            val flingMinDistance = 100f
            val flingMinVelocity = 200f
            if (e1.x - e2.x > flingMinDistance
                && abs(velocityX) > flingMinVelocity
            ) {
                ALog.i("MultiTouchZoomableImageView", "Fling Left")
            } else if (e2.x - e1.x > flingMinDistance
                && abs(velocityX) > flingMinVelocity
            ) {
                ALog.i("MultiTouchZoomableImageView", "Fling Right")
            } else if (e1.y - e2.y > flingMinDistance
                && abs(velocityY) > flingMinVelocity
            ) {
                ALog.i("MultiTouchZoomableImageView", "Fling Up")
            } else if (e2.y - e1.y > flingMinDistance
                && abs(velocityY) > flingMinVelocity
            ) {
                ALog.i("MultiTouchZoomableImageView", "Fling Down")
                if (!transIgnoreScale && getScale() <= zoomDefault()) {
                    mImageGestureListener!!.onImageGestureFlingDown()
                    return true
                }
            }
            try {
                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y
                if (abs(velocityX) > 800 || abs(velocityY) > 800) {
                    scrollBy(diffX / 2, diffY / 2, 300f)
                    invalidate()
                }
            } catch (e: NullPointerException) {
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        try {
            if (mViewPager != null) {
                when (event.action) {
                    MotionEvent.ACTION_MOVE -> mViewPager!!.requestDisallowInterceptTouchEvent(true)
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        mViewPager!!.requestDisallowInterceptTouchEvent(false)
                        scaleRecognized = false
                    }
                }
            }

            // If the bitmap was set, check the scale and gesture detectors
            if (mBitmap != null) {
                // Check the scale detector
                mScaleDetector!!.onTouchEvent(event)

                // Check the gesture detector
                if (!mScaleDetector!!.isInProgress) mGestureDetector!!.onTouchEvent(event)
            } else {
                mImageGestureListener!!.onImageGestureSingleTapConfirmed()
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }
}
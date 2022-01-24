/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.ui.clazz.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.os.Build
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.viewpager.widget.ViewPager
import com.netease.yunxin.app.wisdom.base.util.SampleSizeUtil
import com.netease.yunxin.kit.alog.ALog

abstract class BaseZoomableImageView : View {
    // This is the base transformation which is used to show the image
    // initially.  The current computation for this shows the image in
    // it's entirety, letterboxing as needed.  One could choose to
    // show the image as cropped instead.
    //
    // This matrix is recomputed when we go from the thumbnail image to
    // the full size image.
    private val mBaseMatrix = Matrix()

    // This is the supplementary transformation which reflects what
    // the user has done in terms of zooming and panning.
    //
    // This matrix remains the same when we go from the thumbnail image
    // to the full size image.
    private val mSuppMatrix = Matrix()

    // This is the final matrix which is computed as the concatentation
    // of the base matrix and the supplementary matrix.
    private val mDisplayMatrix: Matrix = Matrix()

    // A replacement ImageView matrix
    private val mMatrix = Matrix()

    // Used to filter the bitmaps when hardware acceleration is not enabled
    private var mPaint: Paint? = null

    // Temporary buffer used for getting the values out of a matrix.
    private val mMatrixValues = FloatArray(9)

    // Dimensions for the view
    private var mThisWidth = -1
    private var mThisHeight = -1

    // The max zoom for the view, determined programatically
    private var mMaxZoom = 0f

    // If not null, calls setImageBitmap when onLayout is triggered
    private var mOnLayoutRunnable: Runnable? = null

    // Stacked to the internal queue to invalidate the view
    private var mRefresh: Runnable? = null

    // The time of the last draw operation
    private var mLastDraw = 0.0

    // The current bitmap being displayed.
    protected var mBitmap: Bitmap? = null

    // Stacked to the internal queue to scroll the view
    private var mFling: Runnable? = null
    private var fling = false

    // Single tap listener
    var mImageGestureListener: ImageGestureListener? = null
    protected var mViewPager: ViewPager? = null
    private var landscape = false

    // Programatic entry point
    constructor(context: Context) : super(context) {
        initBaseZoomableImageView(context)
    }

    // XML entry point
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initBaseZoomableImageView(context)
    }

    // Setup the view
    @SuppressLint("NewApi")
    protected fun initBaseZoomableImageView(context: Context) {
        mPaint = Paint()
        mPaint!!.isDither = true
        mPaint!!.isFilterBitmap = true
        mPaint!!.isAntiAlias = true
        landscape = context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        // Setup the refresh runnable
        mRefresh = Runnable { postInvalidate() }
    }

    // Set the single tap listener
    fun setImageGestureListener(listener: ImageGestureListener?) {
        mImageGestureListener = listener
    }

    fun setViewPager(viewPager: ViewPager?) {
        mViewPager = viewPager
    }

    // Sets the bitmap for the image and resets the base
    // Get the bitmap for the view
    var imageBitmap: Bitmap?
        get() = mBitmap
        set(bitmap) {
            setImageBitmap(bitmap, true)
        }

    // Free the bitmaps and matrices
    fun clear() {
        if (mBitmap != null && !mBitmap!!.isRecycled) {
            mBitmap!!.recycle()
        }
        mBitmap = null
    }

    // When the layout is calculated, set the
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mThisWidth = right - left
        mThisHeight = bottom - top
        val r = mOnLayoutRunnable
        if (r != null) {
            mOnLayoutRunnable = null
            r.run()
        }
        //		if (mBitmap != null) {
        //			setBaseMatrix(mBitmap, mBaseMatrix);
        //			setImageMatrix(getImageViewMatrix());
        //		}
    }

    // Identical to the setImageMatrix method in ImageView
    private fun setImageMatrix(m: Matrix?) {
        var m = m
        if (m != null && m.isIdentity) {
            m = null
        }

        // don't invalidate unless we're actually changing our matrix
        if (m == null && !mMatrix.isIdentity || m != null && mMatrix != m) {
            mMatrix.set(m)
            invalidate()
        }
    }

    // Sets the bitmap for the image and resets the base
    @SuppressLint("NewApi")
    fun setImageBitmap(bitmap: Bitmap?, fitScreen: Boolean? = true) {

        //The version is too old or the length exceeds the maximum texture limit. hardware decoding is not supported。
        if (Build.VERSION.SDK_INT >= ENABLE_LAYER_TYPE_HARDWARE) {
            if (bitmap != null && (bitmap.height > SampleSizeUtil.getTextureSize()
                        || bitmap.width > SampleSizeUtil.getTextureSize())
            ) {
                setLayerType(LAYER_TYPE_SOFTWARE, null)
            } else {
                setLayerType(LAYER_TYPE_HARDWARE, null)
            }
        }
        val viewWidth = width
        if (viewWidth <= 0) {
            mOnLayoutRunnable = Runnable { setImageBitmap(bitmap, fitScreen) }
            return
        }
        val oldBitmap = mBitmap
        mBitmap = if (bitmap != null) {
            setBaseMatrix(bitmap, mBaseMatrix)
            bitmap
        } else {
            mBaseMatrix.reset()
            bitmap
        }
        if (oldBitmap != null && oldBitmap != mBitmap && !oldBitmap.isRecycled) {
            oldBitmap.recycle()
        }
        mSuppMatrix.reset()
        setImageMatrix(getImageViewMatrix())
        mMaxZoom = maxZoom()

        // Set the image to fit the screen
        if (fitScreen == true) {
            zoomToScreen()
        }
    }

    /**
     * Sets the bitmap for the image and resets the base
     *
     * @param bitmap
     * @param selection
     * @date 2014-4-29
     */
    fun setImageBitmap(bitmap: Bitmap?, selection: Rect?) {
        val viewWidth = width
        if (viewWidth <= 0) {
            mOnLayoutRunnable = Runnable { setImageBitmap(bitmap, updateSelection()) }
            return
        }
        val oldBitmap = mBitmap
        mBitmap = if (bitmap != null) {
            setBaseMatrix(bitmap, mBaseMatrix, selection)
            bitmap
        } else {
            mBaseMatrix.reset()
            bitmap
        }
        if (oldBitmap != null && !oldBitmap.isRecycled) {
            oldBitmap.recycle()
        }
        mSuppMatrix.reset()
        setImageMatrix(getImageViewMatrix())
        mMaxZoom = maxZoom()
    }

    // Unchanged from ImageViewTouchBase
    // Center as much as possible in one or both axis.  Centering is
    // defined as follows:  if the image is scaled down below the
    // view's dimensions then center it (literally).  If the image
    // is scaled larger than the view and is translated out of view
    // then translate it back into view (i.e. eliminate black bars).
    protected fun center(vertical: Boolean, horizontal: Boolean, animate: Boolean) {
        if (mBitmap == null) return
        val m = getImageViewMatrix()
        val topLeft = floatArrayOf(0f, 0f)
        val botRight = floatArrayOf(mBitmap!!.width.toFloat(), mBitmap!!.height.toFloat())
        translatePoint(m, topLeft)
        translatePoint(m, botRight)
        val height = botRight[1] - topLeft[1]
        val width = botRight[0] - topLeft[0]
        var deltaX = 0f
        var deltaY = 0f
        if (vertical) {
            val viewHeight = getHeight()
            when {
                height < viewHeight -> {
                    deltaY = (viewHeight - height) / 2 - topLeft[1]
                }
                topLeft[1] > 0 -> {
                    deltaY = -topLeft[1]
                }
                botRight[1] < viewHeight -> {
                    deltaY = getHeight() - botRight[1]
                }
            }
        }
        if (horizontal) {
            val viewWidth = getWidth()
            when {
                width < viewWidth -> {
                    deltaX = (viewWidth - width) / 2 - topLeft[0]
                }
                topLeft[0] > 0 -> {
                    deltaX = -topLeft[0]
                }
                botRight[0] < viewWidth -> {
                    deltaX = viewWidth - botRight[0]
                }
            }
        }
        postTranslate(deltaX, deltaY)
        if (animate) {
            val a: Animation = TranslateAnimation(-deltaX, 0F, -deltaY, 0F)
            a.startTime = SystemClock.elapsedRealtime()
            a.duration = 250
            animation = a
        }
        setImageMatrix(getImageViewMatrix())
    }

    // Unchanged from ImageViewTouchBase
    protected fun getValue(matrix: Matrix, whichValue: Int): Float {
        matrix.getValues(mMatrixValues)
        return mMatrixValues[whichValue]
    }

    // Get the scale factor out of the matrix.
    protected fun getScale(matrix: Matrix): Float {

        // If the bitmap is set return the scale
        return if (mBitmap != null) getValue(matrix, Matrix.MSCALE_X) else 1f
    }

    // Returns the current scale of the view
    open fun getScale(): Float {
        return getScale(mSuppMatrix)
    }

    // Setup the base matrix so that the image is centered and scaled properly.
    private fun setBaseMatrix(bitmap: Bitmap, matrix: Matrix) {
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        matrix.reset()
        val widthScale = (viewWidth / bitmap.width.toFloat()).coerceAtMost(1.0f)
        val heightScale = (viewHeight / bitmap.height.toFloat()).coerceAtMost(1.0f)
        val scale: Float = if (widthScale > heightScale) {
            heightScale
        } else {
            widthScale
        }
        matrix.setScale(scale, scale)
        matrix.postTranslate(
            (viewWidth - bitmap.width.toFloat() * scale) / 2f,
            (viewHeight - bitmap.height.toFloat() * scale) / 2f
        )
    }

    /**
     * Setup the base matrix so that the image is centered and scaled properly.
     * Set the initial display matrix based on Bitmap and Rect
     *
     * @param bitmap
     * @param matrix
     * @param selection
     * @author Linleja
     * @date 2014-4-29
     */
    private fun setBaseMatrix(bitmap: Bitmap, matrix: Matrix, selection: Rect?) {
        if (selection == null) {
            return
        }
        val viewWidth = (selection.right - selection.left).toFloat()
        val viewHeight = (selection.bottom - selection.top).toFloat()
        matrix.reset()
        val widthRatio = viewWidth / bitmap.width.toFloat()
        val heighRatio = viewHeight / bitmap.height.toFloat()
        var scale = 1.0f
        scale = if (widthRatio > heighRatio) {
            widthRatio
        } else {
            heighRatio
        }
        matrix.setScale(scale, scale)
        matrix.postTranslate(
            (width - bitmap.width.toFloat() * scale) / 2f,
            (height - bitmap.height.toFloat() * scale) / 2f
        )
    }

    // Combine the base matrix and the supp matrix to make the final matrix.
    // Combine the base matrix and the supp matrix to make the final matrix.
    protected open fun getImageViewMatrix(): Matrix? {
        mDisplayMatrix.set(mBaseMatrix)
        mDisplayMatrix.postConcat(mSuppMatrix)
        return mDisplayMatrix
    }

    // Sets the maximum zoom, which is a scale relative to the base matrix. It is calculated to show
    // the image at 400% zoom regardless of screen or image orientation. If in the future we decode
    // the full 3 megapixel image, rather than the current 1024x768, this should be changed down to
    // 200%.
    protected fun maxZoom(): Float {
        if (mBitmap == null) return 1f
        val fw = mBitmap!!.width.toFloat() / mThisWidth.toFloat()
        val fh = mBitmap!!.height.toFloat() / mThisHeight.toFloat()
        var max = fw.coerceAtLeast(fh) * 16


        //Set the minimum zoom
        if (max < 1f) {
            max = 1f
        }
        return max
    }

    // Tries to make best use of the space by zooming the picture
    fun zoomDefault(): Float {
        if (mBitmap == null) return 1f
        val fw = mThisWidth.toFloat() / mBitmap!!.width.toFloat()
        val fh = mThisHeight.toFloat() / mBitmap!!.height.toFloat()
        return fw.coerceAtMost(fh).coerceAtLeast(1f)
    }

    // Unchanged from ImageViewTouchBase
    protected open fun zoomTo(scale: Float, centerX: Float, centerY: Float) {
        var scale = scale
        if (scale > mMaxZoom) {
            scale = mMaxZoom
        }
        val oldScale = getScale()
        val deltaScale = scale / oldScale
        mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY)
        setImageMatrix(getImageViewMatrix())
        center(vertical = true, horizontal = true, animate = false)
    }

    // Unchanged from ImageViewTouchBase
    protected open fun zoomTo(scale: Float, centerX: Float, centerY: Float, durationMs: Float) {
        val incrementPerMs = (scale - getScale()) / durationMs
        val oldScale = getScale()
        val startTime = System.currentTimeMillis()

        // Setup the zoom runnable
        post(object : Runnable {
            override fun run() {
                val now = System.currentTimeMillis()
                val currentMs = durationMs.coerceAtMost((now - startTime).toFloat())
                val target = oldScale + incrementPerMs * currentMs
                zoomTo(target, centerX, centerY)
                if (currentMs < durationMs) {
                    post(this)
                }
            }
        })
    }

    private var adjustLongImageEnable = true
    fun setAdjustLongImageEnable(enable: Boolean) {
        adjustLongImageEnable = enable
    }

    fun zoomToScreen() {
        if (mBitmap == null) return
        var scale = 1f
        val fw = mThisWidth.toFloat() / mBitmap!!.width.toFloat()
        var needAdjust = false
        if (adjustLongImageEnable) {
            //If height is greater than width, the screen is in landscape mode.
            if (mBitmap!!.height.toFloat() / mBitmap!!.width.toFloat() > MAX_IMAGE_RATIO_LARGE) {
                needAdjust = true
                scale = fw
            } else if (landscape && mBitmap!!.height.toFloat() / mBitmap!!.width.toFloat() > MAX_IMAGE_RATIO_WIDTH_LARGE_LANDSCAPE) {
                needAdjust = true
                scale = fw
            }
        }
        if (needAdjust) {
            val oldScale = scale
            val deltaScale = scale / oldScale
            mBaseMatrix.reset()
            mSuppMatrix.postScale(deltaScale, deltaScale, 0f, 0f)
            setImageMatrix(getImageViewMatrix())
        } else {
            zoomTo(zoomDefault())
        }
    }

    // Unchanged from ImageViewTouchBase
    fun zoomTo(scale: Float) {
        val width = width.toFloat()
        val height = height.toFloat()
        zoomTo(scale, width / 2f, height / 2f)
    }

    // Unchanged from ImageViewTouchBase
    fun zoomIn() {
        zoomIn(sScaleRate)
    }

    // Unchanged from ImageViewTouchBase
    fun zoomOut() {
        zoomOut(sScaleRate)
    }

    // Unchanged from ImageViewTouchBase
    protected fun zoomIn(rate: Float) {
        if (getScale() >= mMaxZoom) {
            return  // Don't let the user zoom into the molecular level.
        }
        if (mBitmap == null) {
            return
        }
        val width = width.toFloat()
        val height = height.toFloat()
        mSuppMatrix.postScale(rate, rate, width / 2f, height / 2f)
        setImageMatrix(getImageViewMatrix())
    }

    // Unchanged from ImageViewTouchBase
    protected fun zoomOut(rate: Float) {
        if (mBitmap == null) {
            return
        }
        val width = width.toFloat()
        val height = height.toFloat()
        val tmp = Matrix(mSuppMatrix)
        tmp.postScale(1f / sScaleRate, 1f / sScaleRate, width / 2f, height / 2f)
        if (getScale(tmp) < 1f) {
            mSuppMatrix.setScale(1f, 1f, width / 2f, height / 2f)
        } else {
            mSuppMatrix.postScale(1f / rate, 1f / rate, width / 2f, height / 2f)
        }
        setImageMatrix(getImageViewMatrix())
        center(vertical = true, horizontal = true, animate = false)
    }

    // Unchanged from ImageViewTouchBase
    protected fun postTranslate(dx: Float, dy: Float): Boolean {
        return mSuppMatrix.postTranslate(dx, dy)
    }

    // Fling a view by a distance over time
    protected fun scrollBy(distanceX: Float, distanceY: Float, durationMs: Float) {
        val startTime = System.currentTimeMillis()
        mFling = object : Runnable {
            var old_x = 0f
            var old_y = 0f
            override fun run() {
                val now = System.currentTimeMillis()
                val currentMs = durationMs.coerceAtMost((now - startTime).toFloat())
                val x = easeOut(currentMs, 0f, distanceX, durationMs)
                val y = easeOut(currentMs, 0f, distanceY, durationMs)
                postTranslate(x - old_x, y - old_y)
                center(vertical = true, horizontal = true, animate = false)
                old_x = x
                old_y = y
                if (currentMs < durationMs) {
                    fling = post(this)
                } else {
                    stopFling()
                }
            }
        }
        fling = post(mFling)
    }

    protected fun stopFling() {
        removeCallbacks(mFling)
        if (fling) {
            fling = false
            onScrollFinish()
        }
    }

    protected fun fling(): Boolean {
        return fling
    }

    // Gradually slows down a fling velocity
    private fun easeOut(time: Float, start: Float, end: Float, duration: Float): Float {
        var time = time
        return end * ((time / duration - 1.also { time = it.toFloat() }) * time * time + 1) + start
    }

    protected fun onScrollFinish() {}

    // Custom draw operation to draw the bitmap using mMatrix
    @SuppressLint("NewApi")
    override fun onDraw(canvas: Canvas) {
        // Check if the bitmap was ever set
        if (mBitmap != null && !mBitmap!!.isRecycled) {

            // If the current version is above Gingerbread and the layer type is
            // hardware accelerated, the paint is no longer needed
            if (Build.VERSION.SDK_INT >= ENABLE_LAYER_TYPE_HARDWARE
                && layerType == LAYER_TYPE_HARDWARE
            ) {
                canvas.drawBitmap(mBitmap!!, mMatrix, null)
            } else {
                // Check if the time between draws has been met and draw the bitmap
                if (System.currentTimeMillis() - mLastDraw > sPaintDelay) {
                    canvas.drawBitmap(mBitmap!!, mMatrix, mPaint)
                    mLastDraw = System.currentTimeMillis().toDouble()
                } else {
                    canvas.drawBitmap(mBitmap!!, mMatrix, null)
                    removeCallbacks(mRefresh)
                    postDelayed(mRefresh, sPaintDelay.toLong())
                }
            }
        }
    }

    protected fun isScrollOver(distanceX: Float): Boolean {
        try {
            if (mDisplayMatrix != null) {
                val mX = getValue(mDisplayMatrix, Matrix.MTRANS_X) //The offset between the left side of an image and the screen
                val width = width - mX
                //width represents the screen width plus the left offset
                //mBitmap.getWidth() * getValue(mDisplayMatrix, Matrix.MSCALE_X) represents current display width of the image
                //width == mBitmap.getWidth() * getValue(mDisplayMatrix, Matrix.MSCALE_X) indicates that the right offset == 0，and the image reaches the far right
                if (mX == 0f && distanceX <= 0 //Reach the far left of the image and continues swiping
                    || (width == mBitmap!!.width //Reach the far right of the image and continue swiping
                            * getValue(mDisplayMatrix, Matrix.MSCALE_X) && distanceX >= 0)
                ) {
                    ALog.d("ScrollOver")
                    return true
                }
            }
        } catch (e: IllegalArgumentException) {
            ALog.v("Vincent", "isScrollOver")
            e.printStackTrace()
        }
        return false
    }

    private fun updateSelection(): Rect? {
        return null
    }

    companion object {
        // Statics
        const val sPanRate = 7f
        const val sScaleRate = 1.25f
        const val sPaintDelay = 250
        const val sAnimationDelay = 500
        const val ENABLE_LAYER_TYPE_HARDWARE = Build.VERSION_CODES.ICE_CREAM_SANDWICH

        // Special handling for landscape mode. If the height is twice the length of wdth, the image is padded in width
        private const val MAX_IMAGE_RATIO_WIDTH_LARGE_LANDSCAPE = 2f
        private const val MAX_IMAGE_RATIO_LARGE = 5f

        // Translate a given point through a given matrix.
        protected fun translatePoint(matrix: Matrix?, xy: FloatArray?) {
            matrix!!.mapPoints(xy)
        }
    }
}
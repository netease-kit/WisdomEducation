/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.record.video.core.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import com.netease.yunxin.app.wisdom.record.video.sdk.model.VideoScaleMode;
import com.netease.yunxin.kit.alog.ALog;


/**
 * @author netease
 * 单一TextureView
 * 适用于播放页面只有一个TextureView时，支持后台播放
 */

public class BaseTextureView extends TextureView implements IRenderView, TextureView.SurfaceTextureListener {
    private static final String TAG = "BaseSingleTextureView";

    /// callback
    private SurfaceCallback mCallback;

    //Surface Texture
    private SurfaceTexture mSavedSurfaceTexture;

    /// surface holder state
    private Surface mSurface;

    private volatile boolean mReleased;

    private boolean mSizeChanged;
    private int mWidth;
    private int mHeight;

    /// measure
    private MeasureHelper mMeasureHelper;

    /**
     * ******************************** 构造器 ****************************
     */

    public BaseTextureView(Context context) {
        super(context);
        init();
    }

    public BaseTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mMeasureHelper = new MeasureHelper(this);
        setSurfaceTextureListener(this);
    }

    /**
     * *********************************** IRenderView **********************************
     */

    @Override
    public void onSetupRenderView() {

    }

    @Override
    public void setCallback(SurfaceCallback callback) {
        if (mCallback != null || callback == null) {
            return; // 已经注册过的或者null注册的，直接返回
        }

        mCallback = callback;
        if (mSurface != null) {
            mCallback.onSurfaceCreated(getSurface());
        }

        if (mSizeChanged) {
            mCallback.onSurfaceSizeChanged(getSurface(), 0, mWidth, mHeight);
        }
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    public void showView(boolean show) {
        setVisibility(show ? VISIBLE : GONE);
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight, int videoSarNum, int videoSarDen, VideoScaleMode scaleMode) {
        boolean changed = false;

        if (videoWidth > 0 && videoHeight > 0 && mMeasureHelper.setVideoSize(videoWidth, videoHeight)) {
            changed = true;
        }

        if (videoSarNum > 0 && videoSarDen > 0 && mMeasureHelper.setVideoSampleAspectRatio(videoSarNum, videoSarDen)) {
            changed = true;
        }

        if (scaleMode != null && mMeasureHelper.setVideoScaleMode(scaleMode)) {
            changed = true;
        }

        if (changed) {
            ALog.i("set video size to render view done, request layout...");
            requestLayout();
        }
    }

    @Override
    public Surface getSurface() {
        return mSurface;
    }

    public void releaseSurface() {
        mReleased = true;
    }

    private void releaseSurfaceInternal() {
        ALog.d(TAG, "release surfaceTexture=" + mSavedSurfaceTexture);

        if (mSavedSurfaceTexture != null && mSurface != null) {
            if (mCallback != null) {
                mCallback.onSurfaceDestroyed(null);
            }
        }

        if (mSavedSurfaceTexture != null) {
            mSavedSurfaceTexture.release();
        }
        mSavedSurfaceTexture = null;

        if (mSurface != null) {
            mSurface.release();
        }
        mSurface = null;
    }

    @Override
    protected void onDetachedFromWindow() {
        ALog.d(TAG, "onDetachedFromWindow");
        super.onDetachedFromWindow();
        releaseSurface();
    }

    /**
     * *********************************** SurfaceTextureListener **********************************
     */

    @SuppressLint("NewApi")
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        ALog.d(TAG, "onSurfaceTextureAvailable surfaceTexture=" + surfaceTexture + " this=" + this);
        //api 16 以上才能支持 setSurfaceTexture 接口，才能支持后台播放
        boolean hasSetsavedSurfaceTexture = false;
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) && mSavedSurfaceTexture != null) {
            try {
                setSurfaceTexture(mSavedSurfaceTexture);
                hasSetsavedSurfaceTexture = true;
                if (mSavedSurfaceTexture != surfaceTexture) {
                    ALog.d(TAG, "release surfaceTexture=" + surfaceTexture);
                    surfaceTexture.release();
                }
            } catch (IllegalArgumentException e) {
                ALog.e(TAG, "onSurfaceTextureAvailable, setSurfaceTexture ", e);
            }
        }
        if (!hasSetsavedSurfaceTexture) {
            releaseSurfaceInternal();
            mSavedSurfaceTexture = surfaceTexture;
            mSurface = new Surface(surfaceTexture);
            if (mCallback != null) {
                mCallback.onSurfaceCreated(mSurface);
            }
        }
        mSizeChanged = false;
        mWidth = 0;
        mHeight = 0;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        ALog.d(TAG, "onSurfaceTextureSizeChanged " + width + "x" + height + " surfaceTexture=" + surface + " this=" + this);
        mSizeChanged = true;
        mWidth = width;
        mHeight = height;

        if (mCallback != null) {
            mCallback.onSurfaceSizeChanged(mSurface, 0, width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        ALog.d(TAG, "onSurfaceTextureDestroyed surfaceTexture=" + surfaceTexture + " this=" + this);
        mSizeChanged = false;
        mWidth = 0;
        mHeight = 0;

        // 如在该回调前调用releaseSurface则在此释放，否则依靠垃圾回收机制
        if (mReleased) {
            releaseSurfaceInternal();
        }

        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean valid = mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        if (valid) {
            setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}

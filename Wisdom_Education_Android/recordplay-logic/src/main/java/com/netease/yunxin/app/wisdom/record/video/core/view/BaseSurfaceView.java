/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.record.video.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.netease.yunxin.app.wisdom.record.video.sdk.model.VideoScaleMode;
import com.netease.yunxin.kit.alog.ALog;


/**
 * @author netease
 */

public class BaseSurfaceView extends SurfaceView implements IRenderView, SurfaceHolder.Callback {
    /// callback
    private SurfaceCallback mCallback;

    /// surface holder state
    private SurfaceHolder mSurfaceHolder;
    private boolean mSizeChanged;
    private int mFormat;
    private int mWidth;
    private int mHeight;

    /// measure
    private MeasureHelper mMeasureHelper;

    /// show/hide
    private ViewGroup.LayoutParams showLayoutParams;
    private ViewGroup.LayoutParams hideLayoutParams;

    /**
     * ******************************** IRenderView ****************************
     */

    @Override
    public void onSetupRenderView() {
        showLayoutParams = getLayoutParams();
    }

    @Override
    public void setCallback(SurfaceCallback callback) {
        if (mCallback != null || callback == null) {
            return; // 已经注册过的或者null注册的，直接返回
        }

        mCallback = callback;
        if (mSurfaceHolder != null) {
            mCallback.onSurfaceCreated(getSurface());
        }

        if (mSizeChanged) {
            mCallback.onSurfaceSizeChanged(getSurface(), mFormat, mWidth, mHeight);
        }
    }

    @Override
    public void removeCallback() {
        mCallback = null;
    }

    @Override
    public void showView(boolean show) {
        if (show) {
            setLayoutParams(showLayoutParams);
            ALog.i("show view");
        } else {
            if (hideLayoutParams == null) {
                if (showLayoutParams instanceof FrameLayout.LayoutParams) {
                    hideLayoutParams = new FrameLayout.LayoutParams(0, 0);
                } else if (showLayoutParams instanceof RelativeLayout.LayoutParams) {
                    hideLayoutParams = new RelativeLayout.LayoutParams(0, 0);
                } else if (showLayoutParams instanceof LinearLayout.LayoutParams) {
                    hideLayoutParams = new LinearLayout.LayoutParams(0, 0);
                }
            }

            if (hideLayoutParams != null) {
                setLayoutParams(hideLayoutParams);
                ALog.i("hide view");
            } else {
                ALog.i("unsupported layout for hide view!!!");
            }
        }
    }

    @Override
    public void setVideoSize(int videoWidth, int videoHeight, int videoSarNum, int videoSarDen, VideoScaleMode scaleMode) {
        boolean changed = false;

        if (videoWidth > 0 && videoHeight > 0 && mMeasureHelper.setVideoSize(videoWidth, videoHeight)) {
            getHolder().setFixedSize(videoWidth, videoHeight); // 确定Surface窗口的的大小，告知系统视频帧的真实分辨率！！！
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
        return mSurfaceHolder != null ? mSurfaceHolder.getSurface() : null;
    }

    /**
     * ******************************** 构造器 ****************************
     */

    public BaseSurfaceView(Context context) {
        super(context);
        init();
    }

    public BaseSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mMeasureHelper = new MeasureHelper(this);
        getHolder().addCallback(this);
    }

    /**
     * ******************************** SurfaceHolderCallback ****************************
     */

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        ALog.i("surfaceCreated");
        mSurfaceHolder = holder;
        mSizeChanged = false;
        mFormat = 0;
        mWidth = 0;
        mHeight = 0;

        if (mCallback != null) {
            mCallback.onSurfaceCreated(holder != null ? holder.getSurface() : null);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        ALog.i("surfaceDestroyed");
        mSurfaceHolder = null;
        mSizeChanged = false;
        mFormat = 0;
        mWidth = 0;
        mHeight = 0;

        if (mCallback != null) {
            mCallback.onSurfaceDestroyed(holder != null ? holder.getSurface() : null);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        ALog.i("surfaceChanged " + width + "x" + height);
        mSurfaceHolder = holder;
        mSizeChanged = true;
        mFormat = format;
        mWidth = width;
        mHeight = height;

        if (mCallback != null) {
            mCallback.onSurfaceSizeChanged(holder != null ? holder.getSurface() : null, format, width, height);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
    }
}

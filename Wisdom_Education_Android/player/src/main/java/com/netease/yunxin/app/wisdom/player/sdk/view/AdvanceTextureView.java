/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.player.sdk.view;

import android.content.Context;
import android.util.AttributeSet;

import com.netease.yunxin.app.wisdom.player.view.BaseTextureView;


/**
 * TextureView控件
 * 对TextureView做了封装，可直接用于播放器播放，支持后台播放
 *
 * @author netease
 */

public class AdvanceTextureView extends BaseTextureView {
    public AdvanceTextureView(Context context) {
        super(context);
    }

    public AdvanceTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdvanceTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}

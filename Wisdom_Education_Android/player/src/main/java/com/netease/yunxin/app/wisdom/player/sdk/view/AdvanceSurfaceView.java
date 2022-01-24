/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.player.sdk.view;

import android.content.Context;
import android.util.AttributeSet;

import com.netease.yunxin.app.wisdom.player.view.BaseSurfaceView;


/**
 * SurfaceView control
 * SurfaceView is wrapped for the player
 *
 * @author netease
 */

public class AdvanceSurfaceView extends BaseSurfaceView {

    public AdvanceSurfaceView(Context context) {
        super(context);
    }

    public AdvanceSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdvanceSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}

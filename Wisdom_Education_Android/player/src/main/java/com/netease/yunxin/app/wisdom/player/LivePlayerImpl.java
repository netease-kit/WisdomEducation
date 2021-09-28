/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.player;

import android.content.Context;

import com.netease.neliveplayer.sdk.NEMediaDataSource;
import com.netease.yunxin.app.wisdom.player.sdk.model.VideoOptions;

/**
 * @author netease
 */

public class LivePlayerImpl extends AdvanceLivePlayer {

    public LivePlayerImpl(Context context, String videoPath, VideoOptions options) {
        super(context, videoPath, options);
    }

    public LivePlayerImpl(Context context, NEMediaDataSource mediaDataSource, VideoOptions options) {
        super(context, mediaDataSource, options);
    }
}

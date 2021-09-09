/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.record.video.sdk.extension;

import com.netease.yunxin.app.wisdom.record.video.sdk.VodPlayerObserver;
import com.netease.yunxin.app.wisdom.record.video.sdk.model.MediaInfo;

/**
 * @author netease
 */

public abstract class SimpleVodPlayerObserver implements VodPlayerObserver {
    @Override
    public void onNetStateBad() {

    }

    @Override
    public void onSeekCompleted() {

    }

    @Override
    public void onCompletion() {

    }

    @Override
    public void onPreparing() {

    }

    @Override
    public void onPrepared(MediaInfo mediaInfo) {

    }

    @Override
    public void onError(int code, int extra) {

    }

    @Override
    public void onFirstVideoRendered() {

    }

    @Override
    public void onFirstAudioRendered() {

    }

    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingEnd() {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onVideoDecoderOpen(int value) {

    }
}

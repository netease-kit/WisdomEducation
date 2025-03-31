/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.player.sdk.model;

import com.netease.neliveplayer.sdk.model.NEMediaInfo;

/**
 * Media information class
 *
 * @author netease
 */

public class MediaInfo {
    private NEMediaInfo mInner;
    private long mDuration; // VOD

    public MediaInfo(NEMediaInfo mInner, long duration) {
        this.mInner = mInner;
        this.mDuration = duration;
    }

    public long getDuration() {
        return mDuration;
    }

    public String getVideoStreamType() {
        return mInner.mVideoStreamType;
    }

    public String getVideoDecoderMode() {
        return mInner.mVideoDecoderMode;
    }

    public String getAudioStreamType() {
        return mInner.mAudioStreamType;
    }

    public String getAudioDecoderMode() {
        return mInner.mAudioDecoderMode;
    }

    @Override
    public String toString() {
        return "MediaInfo{" +
                "v='" + getVideoStreamType() + '\'' +
                ", '" + getVideoDecoderMode() + '\'' +
                ", a='" + getAudioStreamType() + '\'' +
                ", '" + getAudioDecoderMode() + '\'' +
                ", d='" + mDuration + '\'' +
                '}';
    }
}

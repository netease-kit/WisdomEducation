/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.record.video.core.player;

import android.content.Context;

import com.netease.neliveplayer.sdk.NEMediaDataSource;
import com.netease.yunxin.app.wisdom.record.video.sdk.model.VideoOptions;
import com.netease.yunxin.kit.alog.ALog;


/**
 * @author netease
 * <p>
 * 播放器高级扩展类，在基础扩展类上面封装播放器前后台切换逻辑：
 * <p>
 * 补充：
 * 在后台超过3分钟回到前台就重置播放器
 */

public class AdvanceLivePlayer extends BaseLivePlayer {

    /// constant
    // 如果app应用层没有使用service进行长时间后台播放，这里可以设置为3 * 60 * 1000，在后台超过3分钟回到前台就重置播放器
    // 如果app应用层使用service进行长时间后台播放，这里可以设置为最长的后台播放时长，比如30 * 60 * 1000，在后台超过30分钟回到前台就重置播放器
    private static final long BACKGROUND_RESET_TIME = 30 * 60 * 1000;

    /// 前后台状态及断网重连
    private boolean foreground = true; // app是否在前台(默认在前台)
    private long backgroundTime; // 退到后台的时间点

    AdvanceLivePlayer(Context context, String videoPath, VideoOptions options) {
        super(context, videoPath, options);
    }

    AdvanceLivePlayer(Context context, NEMediaDataSource mediaDataSource, VideoOptions options) {
        super(context, mediaDataSource, options);
    }

    /**
     * ***************************** abstract impl *************************
     */

    @Override
    void onChildInit() {

    }

    @Override
    void onChildDestroy() {

    }

    /**
     * ***************************** player interface *************************
     */

    @Override
    public void onActivityStop(boolean isLive) {
        super.onActivityStop(isLive);
        ALog.i("activity on stop");

        foreground = false; // 切到后台
        backgroundTime = System.currentTimeMillis();
        if (isLive) { //直播
            if (options.hardwareDecode) {
                // 使用硬件解码，直播进入后台停止播放，进入前台重新拉流播放
                ALog.i("force reset live player, as app use hardwareDecode! ");
                resetPlayer();
            } else {
                if (options.isPlayLongTimeBackground) {
                    ALog.i("no reset live player, as app use softwareDecode and isPlayLongTimeBackground is true! ");
                    //使用软件解码，isPlayLongTimeBackground为true，长时间后台播放,直播进入后台不做处理，继续播放，此时需要APP应用层配合使用service来长时间播放，参考demo

                } else {
                    ALog.i("force reset live player, as app use softwareDecode and isPlayLongTimeBackground is false! ");
                    //使用软件解码，isPlayLongTimeBackground为false，后台停止播放,直播进入后台重置
                    resetPlayer();
                }
            }

        } else {  //点播
            if (options.isPlayLongTimeBackground) {
                //使用硬件解码，点播进入后台统一停止播放，进入前台的话重新拉流播放
                if (options.hardwareDecode) {
                    //使用硬件解码，isPlayLongTimeBackground为true，点播进入后台统一停止播放，进入前台的话重新拉流播放
                    ALog.i("force reset vod player, as app use hardwareDecode and isPlayLongTimeBackground is true! ");
                    //因为使用硬件解码在后台播放会在某些机器有兼容性问题，
                    // 使用SurfaceView作为显示控件必需打开下面两行代码，不支持进行后台播放，
                    // 使用TextureView作为显示控件建议：
                    // a.建议打开下面两行代码,硬件解码时重置播放器,不进行后台播放;
                    // b.如果不打开下面两行代码,硬件解码时不会重置播放器，而是后台播放，此时需要忍受在某些机器有兼容性问题（因为TextureView中的surface在不同版本的手机上表现不一样）。
//                    savePlayPosition();
//                    resetPlayer();

                } else {
                    ALog.i("no reset vod player, as app use softwareDecode and isPlayLongTimeBackground is true! ");
                    //使用软件解码，isPlayLongTimeBackground为true，长时间后台播放,点播进入后台不做处理，继续播放，此时需要APP应用层配合使用service来长时间播放，参考demo

                }
            } else {
                //isPlayLongTimeBackground为false,使用软件编码或者硬件解码，点播进入后台暂停，进入前台恢复播放
                ALog.i("pause vod player, as app use softwareDecode or hardwareDecode and isPlayLongTimeBackground is false! ");
                pause();
            }
        }
    }

    @Override
    public void onActivityResume(boolean isLive) {
        super.onActivityResume(isLive);
        ALog.i("activity on resume");
        if (foreground) {
            ALog.i("activity on resume foreground is already true");
            return;
        }

        foreground = true; // 回到前台

        if (player == null) {
            return;
        }

        // 考虑需要重置的场景
        if (!hasReset.get()) {
            final STATE state = getCurrentState().getState();
            if (options.isPlayLongTimeBackground && System.currentTimeMillis() - backgroundTime >= BACKGROUND_RESET_TIME) {
                // 如果在后台时间太长超过了 BACKGROUND_RESET_TIME 的时长且没有重置过，那么立即重置。case: 长时间在后台，超过设置的后台重置时长，在一些极端的情况下播放会停止，但没有收到任何回调，此时回到前台需要重置后重新拉流。
                ALog.i("force reset player, as app on background for a long time! ");
                savePlayerState();
                resetPlayer();
            } else if (state == STATE.PLAYING && !player.isPlaying()) {
                // 当前状态与播放器底层状态不一致，立即重置。
                ALog.i("force reset player, as current state is PLAYING, but player engine is not playing!");
                savePlayerState();
                resetPlayer();
            }
        }

        // 重新恢复拉流视频
        recoverPlayer();
    }


    /**
     * *********************************** core *******************************
     */

    private void recoverPlayer() {
        if (player == null) {
            return;
        }

        if (!hasReset.get() && getCurrentState().getState() != STATE.PAUSED) {
            return; // 没有重置过播放器并且不是点播暂停状态，这里就不需要恢复了
        }


        // 如果播放器已经重置过了，才需要重新初始化。比如退到后台，实际上有Service继续拉流，那么回到前台时，SurfaceView onCreate之后会继续渲染拉流
        ALog.i("recover video from " + "activity on resume" + ", foreground=" + foreground);
        start();
    }
}

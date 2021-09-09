/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package com.netease.yunxin.app.wisdom.record.video.sdk;


import com.netease.neliveplayer.sdk.constant.NEKeyVerifyResultType;

/**
 * 点播播放器状态/事件回调函数观察者
 * 基于播放器SDK封装的点播相关的状态/事件回调
 * <p>
 *
 * @author netease
 */

public interface VodPlayerObserver extends LivePlayerObserver {

    /**
     * [点播专用] 当前播放进度回调，1s回调一次当前播放位置，用于上层进度条控制
     *
     * @param currentPosition 当前播放的毫秒数
     * @param duration        视频源总毫秒数
     * @param percent         进度百分数[0.00-100.00]
     * @param cachedPosition  当前SDK已经缓存到的位置毫秒数
     */
    void onCurrentPlayProgress(long currentPosition, long duration, float percent, long cachedPosition);

    /**
     * [点播专用] 点播seekTo到指定位置播放，跳转成功后触发
     */
    void onSeekCompleted();

    /**
     * [点播专用] 视频播放结束
     * 开发者可以在此告知用户播放结束状态
     */
    void onCompletion();


    /**
     * 音视频不同步回调
     * 在高分辨率时（如：1080p）某些视频播放可能会出现该回调，应用层收到该消息可以进行对应的处理
     * 此时建议使用硬件解码播放，软解在某些性能不高的机器上面会出现音视频不同步
     */
    void onAudioVideoUnsync();

    /**
     * [点播专用]提示网络状态比较差
     * 如果有多种清晰度，在没有开启自动切换清晰度时，建议在此切换到低清晰度；
     * 可以在此在UI上提示用户网络状态较差
     */
    void onNetStateBad();

    /**
     * [点播专用]密钥获取结果
     *
     * @param ret 密钥校验的结果 {@link NEKeyVerifyResultType}
     */
    void onDecryption(int ret);
}

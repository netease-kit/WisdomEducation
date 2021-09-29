/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package  com.netease.yunxin.app.wisdom.player.sdk;


import com.netease.yunxin.app.wisdom.player.sdk.model.MediaInfo;
import com.netease.yunxin.app.wisdom.player.sdk.model.StateInfo;

/**
 * 直播拉流播放器状态/事件回调函数观察者
 * 基于播放器SDK封装的直播相关的状态/事件回调
 * <p>
 *
 * @author netease
 */

public interface LivePlayerObserver {

    /**
     * [重要]正在初始化准备播放
     * 开发者可以在此时做加载、等待动画
     */
    void onPreparing();

    /**
     * [重要]初始化完成并开始播放
     * 开发者可以在此取消加载、等待动画
     */
    void onPrepared(MediaInfo mediaInfo);

    /**
     * [重要]视频播放器出现错误
     * 开发者可以在此结束播放、给予用户出错提示、开启重新播放按钮等
     *
     * @param code  错误码 {@link com.netease.neliveplayer.sdk.constant.NEErrorType}
     *              如果 code=-9999 表示视频码流解析失败，此时音频播放正常，视频可能无画面，开发者可以针对此错误码添加处理逻辑，例如退出、重新播放。
     * @param extra 错误附加信息
     */
    void onError(int code, int extra);

    /**
     * [重要]视频第一帧显示，标志着视频正在播放
     */
    void onFirstVideoRendered();

    /**
     * 音频第一帧显示
     */
    void onFirstAudioRendered();

    /**
     * 视频开始缓冲
     */
    void onBufferingStart();

    /**
     * 视频缓冲结束
     */
    void onBufferingEnd();

    /**
     * 视频缓冲进度
     */
    void onBuffering(int percent);

    /**
     * 硬件解码是否开启
     * value表示是否开启硬件解码，value是1时开启了硬件解码，其他值时开启了软解解码
     */
    void onVideoDecoderOpen(int value);

    /**
     * 播放器状态回调
     *
     * @param stateInfo 播放器当前状态及导致状态的原因
     */
    void onStateChanged(StateInfo stateInfo);

    /**
     * 拉流http状态信息
     *
     * @param code   状态码
     * @param header 头信息
     */
    void onHttpResponseInfo(int code, String header);

}

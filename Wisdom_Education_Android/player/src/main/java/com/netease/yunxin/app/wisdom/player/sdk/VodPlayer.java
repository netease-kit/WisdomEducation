/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package  com.netease.yunxin.app.wisdom.player.sdk;

import com.netease.neliveplayer.sdk.NELivePlayer;
import com.netease.yunxin.app.wisdom.player.sdk.model.DataSourceConfig;


/**
 * 网易云信视频点播播放器接口
 * 基于播放器SDK封装的点播相关的接口
 * <p>
 *
 * @author netease
 */

public abstract class VodPlayer extends LivePlayer {


    /**
     * [点播专用] 获取当前视频的总长度，需要在收到onPrepared的回调后调用有效。
     *
     * @return 点播视频的总长度(单位ms)
     */
    public abstract long getDuration();

    /**
     * [点播专用] 获取当前播放位置的时间点，需要在收到onPrepared的回调后调用有效。
     *
     * @return 当前播放时间(单位ms)
     */
    public abstract long getCurrentPosition();

    /**
     * [点播专用] 获取当前播放进度，需要在收到onPrepared的回调后调用有效。
     *
     * @return 当前播放进度
     */
    public abstract float getCurrentPositionPercent();

    /**
     * [点播专用] 获取当前已经缓存的视频位置，需要在收到onPrepared的回调后调用有效。
     *
     * @return 当前已经缓存的可以直接播放的视频播放位置(单位ms)
     */
    public abstract long getCachedPosition();

    /**
     * [点播专用] 设置到指定时间点播放，需要在收到onPrepared的回调后调用有效。
     *
     * @param position 指定的播放时间位置
     */
    public abstract void seekTo(long position);

    /**
     * [点播专用] 设置播放速度，默认是1.0，范围是[0.5, 2.0]
     *
     * @param speed 速度，常用 0.5/1.0/1.3/1.5/2.0
     */
    public abstract void setPlaybackSpeed(float speed);

    /**
     * [点播专用] 暂停当前播放，调用 start 恢复播放
     */
    public abstract void pause();


    /**
     * [点播专用] 设置缓冲区大小, 必须在setBufferStrategy后调用，reset之后要重新设置
     * NELPANTIJITTER为点播抗抖动模式，点播在该模式下可以设置缓冲区大小，该模式默认缓冲区大小是150M
     * 只针对点播，可设置范围是5M~100M
     *
     * @param size 缓冲区大小,单位byte，设置1M传入1*1024*1024
     */
    public abstract void setBufferSize(int size);

    /**
     * [点播专用] 设置循环播放次数
     *
     * @param loopCount 0，不循环；-1无限循环；1循环一次，2循环两次，以此类推
     */
    public abstract void setLoopCount(int loopCount);

    /**
     * [点播专用] 获取设置的循环播放次数
     *
     * @return 循环次数
     */
    public abstract int getLoopCount();

    /**
     * [点播专用] 设置点播时本地外挂字幕文件
     * 目前只支持SRT格式字幕，SRT文件中的编码只支持UTF-8编码。
     * 设置为 null 关闭字幕。
     * 字幕中的特殊格式需要应用层处理转换为UTF-8编码的SRT格式字幕。
     *
     * @param path 本地外挂字幕文件路径
     */
    public abstract void setSubtitleFile(String path);

    /**
     * [点播专用] 注册一个回调函数，在是否显示外挂字幕时调用
     * 设置外挂字幕路径后才能有回调，参考 {@link VodPlayer#setSubtitleFile}
     *
     * @param listener 是否显示字幕的监听器
     * @param register true表示注册; false表示注销
     */
    public abstract void registerPlayerSubtitleListener(NELivePlayer.OnSubtitleListener listener, boolean register);

    /**
     * [点播专用] 播放过程中切换播放地址，第一次播放不能调用该接口，仅支持当前播放结束切换到下一个视频，或者播放过程中切换下一个视频
     *
     * @param url    播放地址
     * @param config 播放配置项（配置缓存本地信息和解密信息）
     */
    public abstract void switchContentUrl(String url, DataSourceConfig config);


}

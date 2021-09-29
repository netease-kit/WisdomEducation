/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.player;

import android.content.Context;

import com.netease.neliveplayer.sdk.NELivePlayer;
import com.netease.neliveplayer.sdk.NEMediaDataSource;
import com.netease.neliveplayer.sdk.model.NESDKConfig;
import com.netease.yunxin.app.wisdom.player.sdk.LivePlayer;
import com.netease.yunxin.app.wisdom.player.sdk.VodPlayer;
import com.netease.yunxin.app.wisdom.player.sdk.model.SDKInfo;
import com.netease.yunxin.app.wisdom.player.sdk.model.SDKOptions;
import com.netease.yunxin.app.wisdom.player.sdk.model.VideoOptions;

import java.util.ArrayList;
import java.util.Map;

public class PlayerManagerImpl {

    /**
     * 初始化SDK,使用播放器时必须先进行初始化才能进行后续操作。
     *
     * @param context 调用上下文
     * @param options sdk配置信息
     */
    public static void init(Context context, SDKOptions options) {
        NESDKConfig sdkConfig = new NESDKConfig();
        if (options != null) {
            sdkConfig.thirdUserId = options.thirdUserId;
            sdkConfig.refreshPreLoadDuration = options.refreshPreLoadDuration;
            sdkConfig.isCloseTimeOutProtect = options.isCloseTimeOutProtect;
            sdkConfig.dynamicLoadingConfig = options.dynamicLoadingConfig;
            sdkConfig.dataUploadListener = options.dataUploadListener;
            sdkConfig.logListener = options.logListener;
            sdkConfig.supportDecodeListener = options.supportDecodeListener;
            sdkConfig.isCloseTimeOutProtect = false;//这里使用播放器的SDK对准备阶段的超时进行自动处理，如果用户需要自定义实现该超时可以设置true进行关闭，然后参考playkit中的BaseLivePlayer#preparingTimeoutTask实现
            sdkConfig.privateConfig = options.privateConfig;
        }
        NELivePlayer.init(context, sdkConfig);
    }

    /**
     * 获取是否已经准备好so库文件
     * 仅在初始化 init 接口中配置动态加载才能使用该接口查询
     *
     * @return 是否准备好
     */
    public static boolean isDynamicLoadReady() {
        return NELivePlayer.isDynamicLoadReady();
    }

    /**
     * 获取SDK信息
     *
     * @return SDK信息实例
     */
    public static SDKInfo getSDKInfo(Context context) {
        if (NELivePlayer.getSDKInfo(context) == null) {
            return null;
        }
        SDKInfo sdkInfo = new SDKInfo();
        sdkInfo.version = NELivePlayer.getSDKInfo(context).version;
        sdkInfo.deviceId = NELivePlayer.getSDKInfo(context).deviceId;
        return sdkInfo;
    }

    /**
     * 构造播放器实例对象
     *
     * @param context   上下文
     * @param videoPath 视频资源路径
     * @param options   播放选项
     * @return 播放器实例对象
     */
    public static LivePlayer buildLivePlayer(Context context, String videoPath, VideoOptions options) {
        return new LivePlayerImpl(context, videoPath, options);
    }

    /**
     * 构造播放器实例对象
     *
     * @param context   上下文
     * @param videoPath 视频资源路径
     * @param options   播放选项
     * @return 播放器实例对象
     */
    public static VodPlayer buildVodPlayer(Context context, String videoPath, VideoOptions options) {
        return new LivePlayerImpl(context, videoPath, options);
    }

    /**
     * 构造播放器实例对象
     *
     * @param context         上下文
     * @param mediaDataSource 视频资源,需要实现 NEMediaDataSource 自定义数据源
     * @param options         播放选项
     * @return 播放器实例对象
     */
    public static VodPlayer buildVodPlayer(Context context, NEMediaDataSource mediaDataSource, VideoOptions options) {
        return new LivePlayerImpl(context, mediaDataSource, options);
    }


    /**
     * 添加预加载拉流链接地址
     *
     * @param urls 拉流链接地址
     */
    public static void addPreloadUrls(ArrayList<String> urls) {
        NELivePlayer.addPreloadUrls(urls);
    }

    /**
     * 移除预加载拉流链接地址
     *
     * @param urls 拉流链接地址
     */
    public static void removePreloadUrls(ArrayList<String> urls) {
        NELivePlayer.removePreloadUrls(urls);

    }

    /**
     * 查询预加载拉流链接地址的结果信息
     *
     * @return Map<String, Integer> String是链接地址，Integer是状态,状态码参考 {@link com.netease.yunxin.app.wisdom.record.video.sdk.constant.PreloadStatusType}
     */
    public static Map<String, Integer> queryPreloadUrls() {
        return NELivePlayer.queryPreloadUrls();
    }

    /**
     * 刷新全部拉流地址预加载信息
     */
    public static void refreshPreloadUrls() {
        NELivePlayer.refreshPreloadUrls();
    }

}

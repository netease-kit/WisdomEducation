/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package  com.netease.yunxin.app.wisdom.player.sdk.model;

import com.netease.neliveplayer.proxy.config.NEPlayerConfig;
import com.netease.neliveplayer.sdk.NELivePlayer;
import com.netease.neliveplayer.sdk.model.NEDynamicLoadingConfig;

/**
 * SDK配置信息类
 *
 * @author netease
 */

public class SDKOptions {
    /**
     * 应用层传入到SDK中的能区分设备的id值，方便播放器通过该值查询到对应播放数据和日志
     */
    public String thirdUserId;

    /**
     * 预加载刷新间隔
     * 单位：ms，默认30分钟，设置 30 * 60 * 100
     * 涉及的接口 {@link NELivePlayer#addPreloadUrls },{@link NELivePlayer#removePreloadUrls },{@link NELivePlayer#queryPreloadUrls }
     */
    public long refreshPreLoadDuration = 30 * 60 * 1000;

    /**
     * 是否对预处理和缓冲进行超时保护
     * 默认false开启对预处理和缓冲进行超时保护，如果应用层需要自己处理预处理和缓冲的超时逻辑可以设置为true关掉该功能。
     */
    public boolean isCloseTimeOutProtect;

    /**
     * so库动态加载配置信息
     */
    public NEDynamicLoadingConfig dynamicLoadingConfig;

    /**
     * 数据上报回调
     * 目前SDK内部会将统计数据上报到网易云统计平台，如果上层注册了该回调，那么SDK不再进行数据上传而是由上层进行网络请求上传数据。
     */
    public NELivePlayer.OnDataUploadListener dataUploadListener;

    /**
     * 播放器日志回调
     */
    public NELivePlayer.OnLogListener logListener;

    /**
     * 是否支持H265解码回调
     */
    public NELivePlayer.OnSupportDecodeListener supportDecodeListener;

    /**
     * 私有化设置
     */
    public NEPlayerConfig privateConfig;
}

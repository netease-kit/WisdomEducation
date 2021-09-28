/*
 *  Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 *  Use of this source code is governed by a MIT license that can be found in the LICENSE file
 */

package  com.netease.yunxin.app.wisdom.player.sdk.model;

/**
 * 缓存配置信息
 *
 * @author netease
 */
public class CacheConfig {
    /**
     * 是否缓存音视频到本地
     */
    public boolean isCache;
    /**
     * 缓存的路径地址
     * 如果为空，那么内部自动缓存到默认地址，并且每次释放播放器、重置播放器、切换拉流地址会删除缓存的视频文件
     * 如果不为空，那么SDK会根据设置进来的路径进行缓存，APP应用层自行管理缓存的文件
     */
    public String cachePath;

    public CacheConfig(boolean isCache, String cachePath) {
        this.isCache = isCache;
        this.cachePath = cachePath;
    }
}

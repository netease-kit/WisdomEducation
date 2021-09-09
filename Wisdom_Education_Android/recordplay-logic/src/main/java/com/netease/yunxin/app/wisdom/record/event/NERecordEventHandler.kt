/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.record.event

import com.netease.yunxin.app.wisdom.record.model.NERecordEvent
import java.util.*

/**
 * 处理相关事件类型的类，1个EventHandler对应1组相关事件类型
 *
 *
 */
interface NERecordEventHandler {
    /**
     * 过滤事件类型
     *
     * @return 是否允许处理事件
     */
    fun filterType(event: NERecordEvent): Boolean

    /**
     * 拖动进度条处理，更新到currentTime的状态
     * 用户可以自定义处理流程，如从服务端获取snap的方式同步，用本地历史事件队列做兜底
     *
     * @param prevExecutedEventList seek前执行完成事件队列
     * @param executedEventList seek后执行完成事件队列
     */
    fun processSeek(prevExecutedEventList: LinkedList<NERecordEvent>, executedEventList: LinkedList<NERecordEvent>)

    /**
     * 正常播放执行处理每个事件
     *
     * @param event
     */
    fun process(event: NERecordEvent)

    /**
     * 初始化重置
     *
     */
    fun resetToInit()

    /**
     * 过滤其他条件
     *
     */
    fun filterOther(event: NERecordEvent): Boolean
}
/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

/**
 * Created by hzsunyj on 2021/6/1.
 */
enum class NEEduHttpMethod(val method: String) {
    PUT("put"),
    POST("post"),
    GET("get"),
    DELETE("delete"),
    HEADER("header"),
}
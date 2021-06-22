/*
 * Copyright (c) 2021 NetEase, Inc.  All rights reserved.
 * Use of this source code is governed by a MIT license that can be found in the LICENSE file.
 */

package com.netease.yunxin.app.wisdom.edu.logic.model

/**
 * Created by hzsunyj on 2021/5/18.
 */
class NEEduSnapshotRes(val sequence: Long, val snapshot: NEEduSnapshot) {
}

class NEEduSnapshot(val room: NEEduRoom, val members: MutableList<NEEduMember>) {

}
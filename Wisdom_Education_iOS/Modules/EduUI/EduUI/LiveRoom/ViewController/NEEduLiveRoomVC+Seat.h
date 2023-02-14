// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
    
#import "NEEduLiveRoomVC.h"
#import "NEEduSeatService.h"
NS_ASSUME_NONNULL_BEGIN

/// 麦位扩展
@interface NEEduLiveRoomVC (Seat)
///获取麦位请求列表
- (void)getSeatRequestList;
/// 获取麦位信息
- (void)getSeatInfo;
@end

NS_ASSUME_NONNULL_END

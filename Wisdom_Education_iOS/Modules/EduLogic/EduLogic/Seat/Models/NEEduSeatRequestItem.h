// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
    

#import <Foundation/Foundation.h>
#import "NEEduSeatItem.h"

NS_ASSUME_NONNULL_BEGIN

/// 麦位申请信息
@interface NEEduSeatRequestItem : NSObject
/// 房间号
@property(nonatomic, copy) NSString *roomUuid;
/// 用户id
@property(nonatomic, copy) NSString *userUuid;
/// 用户名称
@property(nonatomic, copy) NSString *userName;
/// 用户头像
@property(nonatomic, copy) NSString *icon;
/// 麦位状态
@property(nonatomic, assign) NEEduSeatItemStatus status;

@end

NS_ASSUME_NONNULL_END

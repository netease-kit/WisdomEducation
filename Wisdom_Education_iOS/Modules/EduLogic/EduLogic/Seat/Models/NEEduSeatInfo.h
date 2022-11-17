// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
    

#import <Foundation/Foundation.h>
#import "NEEduSeatItem.h"
#import <YYModel/YYModel.h>

NS_ASSUME_NONNULL_BEGIN

/// 麦位信息
@interface NEEduSeatInfo : NSObject <YYModel>
/// 创建者
@property(nonatomic, copy) NSString *seatCreatorUserUuid;
/// 麦位管理员列表
@property(nonatomic, copy) NSArray *seatManagerUserUuidList;
/// 麦位列表信息
@property(nonatomic, strong) NSArray <NEEduSeatItem *> *seatIndexList;
@end

NS_ASSUME_NONNULL_END

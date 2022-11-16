// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
    

#import <Foundation/Foundation.h>
#import "NEEduSeatEvent.h"
#import "NEEduSeatItem.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduSeatItemDetail : NSObject <YYModel>
@property(nonatomic, copy, nullable) NSString *roomUuid;
@property(nonatomic, copy, nullable) NSString *appId;
@property(nonatomic, copy) NSArray <NEEduSeatItem *> *seatList;
@end


/// 麦位状态变更通知
@interface NEEduSeatItemChangeEvent : NEEduSeatEvent
/// 时间戳
@property(nonatomic, assign) NSTimeInterval timestamp;
@property(nonatomic, strong) NEEduSeatItemDetail *data;
@end

NS_ASSUME_NONNULL_END

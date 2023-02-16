// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
    

#import <Foundation/Foundation.h>
#import <YYModel/YYModel.h>
NS_ASSUME_NONNULL_BEGIN
/// 麦位事件
@interface NEEduSeatEvent : NSObject <YYModel>
/// 房间uuid
@property(nonatomic, copy, nullable) NSString *roomUuid;
/// 消息编号
@property(nonatomic, assign) NSInteger cmd;
@end

NS_ASSUME_NONNULL_END

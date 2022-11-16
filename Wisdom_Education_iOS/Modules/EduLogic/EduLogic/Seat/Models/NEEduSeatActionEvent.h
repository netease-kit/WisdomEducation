// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
    

#import <Foundation/Foundation.h>
#import "NEEduSeatEvent.h"

NS_ASSUME_NONNULL_BEGIN
/// 麦位用户信息
@interface NEEduSeatUserInfo : NSObject
@property(nonatomic, copy) NSString *appId;
@property(nonatomic, copy) NSString *roomUuid;
@property(nonatomic, copy) NSString *userUuid;
@property(nonatomic, copy) NSString *userName;
@property(nonatomic, copy) NSString *icon;
@property(nonatomic, assign) NSTimeInterval updated;
@end
@interface NEEduSeatOperatorUser : NSObject
@property(nonatomic, copy) NSString *userUuid;
@end

@interface NEEduSeatActionDetail : NSObject
/// 麦位上的用户
@property(nonatomic, strong) NEEduSeatUserInfo *seatUser;
/// 操作用户
@property(nonatomic, strong) NEEduSeatOperatorUser *operatorUser;
/// 自动同意
@property(nonatomic, assign) BOOL autoAgree;
@end


/// 麦位操作事件
@interface NEEduSeatActionEvent : NEEduSeatEvent
/// 时间戳
@property(nonatomic, assign) NSTimeInterval timestamp;
@property(nonatomic, strong) NEEduSeatActionDetail *data;

@end

NS_ASSUME_NONNULL_END

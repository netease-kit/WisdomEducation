// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.

#import "NEEduSeatEvent.h"
NS_ASSUME_NONNULL_BEGIN

@interface NEEduSeatManagerChangeDetail : NSObject
@property(nonatomic, copy) NSString *roomUuid;
@property(nonatomic, copy) NSString *userUuid;
@property(nonatomic, assign) NSInteger valid;
@end

@interface NEEduSeatManagerChangeEvent : NEEduSeatEvent
@property(nonatomic, assign) NSTimeInterval timestamp;
@property(nonatomic, strong) NEEduSeatManagerChangeDetail *data;
@end

NS_ASSUME_NONNULL_END

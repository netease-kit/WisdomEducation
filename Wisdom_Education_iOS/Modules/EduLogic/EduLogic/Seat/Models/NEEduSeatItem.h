// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
    

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN


/// 麦位状态
typedef NS_ENUM(NSInteger, NEEduSeatItemStatus) {
    /// 初始化(无人, 可上麦)
    NEEduSeatItemStatusInitial = 0,
    /// 该麦位正在等待管理员通过申请或等待成员接受邀请后上麦
    NEEduSeatItemStatusWaiting,
    /// 当前麦位已被占用
    NEEduSeatItemStatusTaken,
    /// 当前麦位已关闭, 不能操作上麦
    NEEduSeatItemStatusClosed
};
/// 上麦类型
typedef NS_ENUM(NSInteger, NEEduSeatOnSeatType) {
    /// 无效
    NEEduSeatOnSeatTypeInvalid = -1,
    /// 申请上麦
    NEEduSeatOnSeatTypeRequest,
    /// 邀请上麦
    NEEduSeatOnSeatTypeInvitation
};

/// 单个麦位信息
@interface NEEduSeatItem : NSObject
/// 房间id
@property(nonatomic, copy) NSString *roomUuid;
/// 麦位位置
@property(nonatomic, assign) NSInteger seatIndex;
/// 麦位状态
@property(nonatomic, assign) NEEduSeatItemStatus status;
/// 当前状态关联的用户
@property(nonatomic, copy, nullable) NSString *userUuid;
/// 用户名
@property(nonatomic, copy, nullable) NSString *userName;
/// 用户头像
@property(nonatomic, copy, nullable) NSString *icon;
/// 上麦类型
@property(nonatomic, assign) NEEduSeatOnSeatType onSeatType;
/// 时间戳
@property(nonatomic, assign) NSTimeInterval updated;
@end

NS_ASSUME_NONNULL_END

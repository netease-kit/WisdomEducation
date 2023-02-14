// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
    

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/// 用户麦位操作类型
typedef NS_ENUM(NSInteger, NEEduSeatUserActionType) {
    /// 申请麦位
    NEEduSeatUserActionTypeApply = 1,
    /// 取消申请麦位
    NEEduSeatUserActionTypeCancelApply,
    /// 拒绝邀请
    NEEduSeatUserActionTypeRejectInvitation,
    /// 同意邀请
    NEEduSeatUserActionTypeAcceptInvitation,
    /// 主动离开
    NEEduSeatUserActionTypeLeave
};

/// 用户麦位操作
@interface NEEduSeatUserAction : NSObject
/// 操作类型
@property(nonatomic, assign) NEEduSeatUserActionType action;
/// 麦位序号
@property(nonatomic, assign) NSInteger seatIndex;
/// 是否占位
@property(nonatomic, assign) BOOL lockIndex;
+ (instancetype)action:(NEEduSeatUserActionType)actionType;
@end

NS_ASSUME_NONNULL_END

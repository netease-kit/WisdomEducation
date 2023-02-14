// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
    

#import <Foundation/Foundation.h>
#import "HttpManager.h"
#import "NEEduSeatInfo.h"
#import "NEEduSeatRequestItem.h"
NS_ASSUME_NONNULL_BEGIN

@protocol NEEduSeatEventListener <NSObject>
@optional
/// 成员进行麦位申请
- (void)onSeatRequestSubmitted:(NSString *)user;
/// 成员取消麦位申请
- (void)onSeatRequestCancelled:(NSString *)user;
/// 管理员同意成员的麦位申请
- (void)onSeatRequestApproved:(NSString *)user operateBy:(NSString *)operateBy;
/// 管理员拒绝成员的麦位申请申请
- (void)onSeatRequestRejected:(NSString *)user operateBy:(NSString *)operateBy;
/// 成员主动离开位置 seatIndex 麦位
- (void)onSeatLeave:(NSString *)user;
/// 成员被管理员从位置 seatIndex 麦位 踢掉
- (void)onSeatKicked:(NSString *)user operateBy:(NSString *)operateBy;
/// 麦位全量列表变更
- (void)onSeatListChanged:(NSArray <NEEduSeatItem *> *)seatItems;
@end


@interface NEEduSeatService : NSObject
/// 添加麦位事件监听
- (void)addSeatListener:(id <NEEduSeatEventListener>)listener;
/// 移除麦位事件监听
- (void)removeSeatListener:(id <NEEduSeatEventListener>)listener;

/// 获取麦位信息
/// @param roomUuid 房间id
/// @param success 成功回调
/// @param failure 失败回调
- (void)getSeatInfo:(NSString *)roomUuid
            success:(_Nonnull NEEduSuccessBlock)success
            failure:(_Nullable NEEduFailureBlock)failure;

/// 获取麦位申请列表
/// @param roomUuid 房间Id
/// @param success 成功回调
/// @param failure 失败回调
- (void)getSeatRequestList:(NSString *)roomUuid
                   success:(void (^)(NSArray <NEEduSeatRequestItem *> *list))success
                   failure:(_Nullable NEEduFailureBlock)failure;
/// 申请麦位
/// @param roomUuid 房间Id
/// @param success 成功回调
/// @param failure 失败回调
- (void)applySeat:(NSString *)roomUuid
         userName:(NSString *)userName
          success:(void(^ _Nullable)(void))success
          failure:(_Nullable NEEduFailureBlock)failure;

/// 取消申请麦位
/// @param roomUuid 房间Id
/// @param success 成功回调
/// @param failure 失败回调
- (void)cancelApplySeat:(NSString *)roomUuid
               userName:(NSString *)userName
                success:(void(^ _Nullable)(void))success
                failure:(_Nullable NEEduFailureBlock)failure;

/// 主动下麦
/// @param roomUuid 房间id
/// @param success 成功回调
/// @param failure 失败回调
- (void)leaveSeat:(NSString *)roomUuid
         userName:(NSString *)userName
          success:(void(^ _Nullable)(void))success
          failure:(_Nullable NEEduFailureBlock)failure;

/// 接收麦位邀请
/// @param roomUuid 房间id
/// @param success 成功回调
/// @param failure 失败回调
- (void)acceptSeatInvitation:(NSString *)roomUuid
                    userName:(NSString *)userName
                     success:(void(^ _Nullable)(void))success
                     failure:(_Nullable NEEduFailureBlock)failure;

/// 拒绝麦位邀请
/// @param roomUuid 房间id
/// @param success 成功回调
/// @param failure 失败回调
- (void)rejectSeatInvitation:(NSString *)roomUuid
                    userName:(NSString *)userName
                     success:(void(^ _Nullable)(void))success
                     failure:(_Nullable NEEduFailureBlock)failure;
@end

NS_ASSUME_NONNULL_END

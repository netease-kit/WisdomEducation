//
//  NEEduManager.h
//  EduLogic
//
//  Created by Groot on 2021/5/13.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NEEduKitOptions.h"
#import "NEEduUser.h"
#import "NEEduRoomProfile.h"
#import "NEEduEnterRoomParam.h"
#import "NEEduRoomService.h"
#import "NEEduMessageService.h"
#import "NEEduIMService.h"
#import "NEEduRtcService.h"
#import "NEEduUserService.h"
#import "NEEduHttpUser.h"
NS_ASSUME_NONNULL_BEGIN

@interface NEEduManager : NSObject
@property (nonatomic, strong) NEEduIMService *imService;
@property (nonatomic, strong) NEEduRtcService *rtcService;

@property (nonatomic, strong) NEEduRoomService *roomService;
@property (nonatomic, strong) NEEduMessageService *messageService;
@property (nonatomic, strong) NEEduUserService *userService;

@property (nonatomic, strong) NEEduRoomProfile *profile;
@property (nonatomic, strong ,readonly) NEEduHttpUser *localUser;

@property (nonatomic, copy, readonly) NSString *imKey;
@property (nonatomic, assign) BOOL reuseIM;

+ (instancetype)shared;

- (void)setupAppKey:(NSString * _Nonnull)appKey options:(NEEduKitOptions * )options;

/// 登录
/// @param userID 用户ID
/// @param token 用户Token
/// @param success 成功
/// @param failure 失败
- (void)login:(NSString *)userID token:(NSString *)token success:(void(^)(NEEduUser *user))success failure:(void(^)(NSError *error))failure;

// 匿名登录（demo使用）
- (void)easyLoginWithSuccess:(void(^)(NEEduUser *user))success failure:(void(^)(NSError *error))failure;

///加入房间
/// @param roomOption 房间设置
/// @param completion 加入结果
- (void)enterClassroom:(NEEduEnterRoomParam *)roomOption completion:(void(^)(NSError *error,NEEduEnterRoomResponse * response))completion;

/// 加入Rtc房间并获取房间整体信息
/// @param completion 结果
- (void)joinRtcAndGetProfileCompletion:(void(^)(NSError *error,NEEduRoomProfile *profile))completion;

/// 获取房间快照
/// @param completion 返回快照信息
- (void)getProfileCompletion:(void(^)(NSError *error,NEEduRoomProfile *profile))completion;

/// 加入聊天室
/// @param success 成功回调
/// @param failed 失败回调
- (void)joinChatRoomSuccess:(void(^)(NEEduChatRoomResponse *response))success failed:(void(^)(NSError *error))failed;

/// 设置画布
/// @param view 视频渲染View
/// @param member 用户信息
- (void)setCanvasView:(UIView *)view forMember:(NEEduHttpUser *)member;

/// 业务上离开房间但不销毁Rtc实例
- (void)leaveClassroom;

/// 销毁Rtc实例
- (void)destoryClassroom;

@end

NS_ASSUME_NONNULL_END


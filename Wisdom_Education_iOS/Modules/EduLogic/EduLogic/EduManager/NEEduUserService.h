//
//  NEEduUserService.h
//  EduLogic
//
//  Created by Groot on 2021/5/26.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NEEduRoomProfile.h"
#import "NEEduUser.h"
#import "NEEduHandsupProperty.h"

NS_ASSUME_NONNULL_BEGIN

@interface NEEduUserService : NSObject

- (instancetype)initLocalUser:(NEEduUser *)localUser;
- (void)setupProfile:(NEEduRoomProfile *)profile;
//视频开关
- (void)localUserVideoEnable:(BOOL)enable result:(void(^)(NSError *error))result;
//音频开关
- (void)localUserAudioEnable:(BOOL)enable result:(void(^)(NSError *error))result;
//视频可开关授权
- (void)remoteUserVideoEnable:(BOOL)enable userID:(NSString *)userID result:(void(^)(NSError *error))result;
//音频可开关授权
- (void)remoteUserAudioEnable:(BOOL)enable userID:(NSString *)userID result:(void(^)(NSError *error))result;
//屏幕共享授权
- (void)screenShareAuthorization:(BOOL)enable userID:(NSString *)userID result:(void(^)(NSError *error))result;
//发起屏幕共享
- (void)localShareScreenEnable:(BOOL)enable result:(void(^)(NSError *error))result;
//白板可编辑授权
- (void)whiteboardDrawable:(BOOL)drawable userID:(NSString *)userID result:(void(^)(NSError *error))result;
//举手
- (void)handsupStateChange:(NEEduHandsupState)state userID:(NSString *)userID result:(void(^)(NSError *error))result;
/// 是否有人在屏幕共享状态
- (NEEduHttpUser *)userIsShareScreen;


@end

NS_ASSUME_NONNULL_END

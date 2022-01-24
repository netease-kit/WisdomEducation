//
//  NEEduMessageService.h
//  EduLogic
//
//  Created by Groot on 2021/5/24.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NEEduHttpUser.h"
#import "NEEduIMService.h"
#import "NEEduRoomProfile.h"

NS_ASSUME_NONNULL_BEGIN
@protocol NEEduMessageServiceDelegate <NSObject>

/// 用户进入房间
- (void)onUserInWithUser:(NEEduHttpUser *)user members:(NSArray *)members;
/// 用户推出房间
- (void)onUserOutWithUser:(NEEduHttpUser *)user members:(NSArray *)members;

/// 用户token校验失败或token过期，请求中出现校验失败回调到这里
/// @param user 用户信息
- (void)onUserTokenExpired:(NEEduHttpUser *)user;

- (void)onVideoStreamEnable:(BOOL)enable user:(NEEduHttpUser *)user;
- (void)onAudioStreamEnable:(BOOL)enable user:(NEEduHttpUser *)user;
- (void)onSubVideoStreamEnable:(BOOL)enable user:(NEEduHttpUser *)user;

- (void)onVideoAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user;
- (void)onAudioAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user;

- (void)onWhiteboardAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user;
- (void)onScreenShareAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user;
/// 举手
- (void)onHandsupStateChange:(NEEduHandsupState)state user:(NEEduHttpUser *)user;

- (void)onLessonStateChange:(NEEduLessonStep *)step roomUuid:(NSString *)roomUuid;

/// 禁言
- (void)onLessonMuteAllAudio:(BOOL)mute roomUuid:(NSString *)roomUuid;
/// 禁聊
- (void)onLessonMuteAllText:(BOOL)mute roomUuid:(NSString *)roomUuid;

@end


/// 消息服务
@interface NEEduMessageService : NSObject<NEEduIMServiceDelegate>
@property (nonatomic, weak) id delegate;
@property (nonatomic, strong) NEEduHttpUser *localUser;
- (void)updateProfile:(NEEduRoomProfile *)profile;

@end

NS_ASSUME_NONNULL_END

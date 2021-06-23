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

- (void)onUserInWithUser:(NEEduHttpUser *)user members:(NSArray *)members;
- (void)onUserOutWithUser:(NEEduHttpUser *)user members:(NSArray *)members;
- (void)onVideoStreamEnable:(BOOL)enable user:(NEEduHttpUser *)user;
- (void)onAudioStreamEnable:(BOOL)enable user:(NEEduHttpUser *)user;
- (void)onSubVideoStreamEnable:(BOOL)enable user:(NEEduHttpUser *)user;

- (void)onVideoAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user;
- (void)onAudioAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user;

- (void)onWhiteboardAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user;
- (void)onScreenShareAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user;

- (void)onHandsupStateChange:(NEEduHandsupState)state user:(NEEduHttpUser *)user;

- (void)onLessonStateChange:(NEEduLessonStep *)step roomUuid:(NSString *)roomUuid;

- (void)onLessonMuteAllAudio:(BOOL)mute roomUuid:(NSString *)roomUuid;

- (void)onLessonMuteAllText:(BOOL)mute roomUuid:(NSString *)roomUuid;

@end


@interface NEEduMessageService : NSObject<NEEduIMServiceDelegate>
@property (nonatomic, weak) id delegate;
- (void)updateProfile:(NEEduRoomProfile *)profile;
@end

NS_ASSUME_NONNULL_END

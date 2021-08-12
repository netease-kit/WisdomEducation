//
//  NEEduMessageService.m
//  EduLogic
//
//  Created by Groot on 2021/5/24.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduMessageService.h"
#import "NEEduSignalMessage.h"
#import "NEEduSignalUserIn.h"
#import "NEEduSignalStreamOnOff.h"
#import "NEEduSignalProperty.h"
#import "NEEduHttpRoom.h"
#import <YYModel/NSObject+YYModel.h>
#import "HttpManager.h"
#import "NEEduLostMessages.h"
#import "NEEduRemoveProperty.h"

@interface NEEduMessageService ()
@property (nonatomic, assign) NSInteger currentSequence;
@property (nonatomic, strong) NEEduRoomProfile *profileInfo;

@end

@implementation NEEduMessageService

- (void)updateProfile:(NEEduRoomProfile *)profile {
    _profileInfo = profile;
    self.currentSequence = MAX(profile.sequence, self.currentSequence);
}
#pragma mark - NEEduIMServiceDelegate
- (void)didRecieveSignalMessage:(NSString *)message fromUser:(NSString *)fromUser {
    NSLog(@"IM:%@",message);
    NEEduSignalMessage *messageModel = [NEEduSignalMessage yy_modelWithJSON:message];
    //过滤不同房间的消息
    if (![self.profileInfo.snapshot.room.roomUuid isEqualToString:messageModel.roomUuid]) {
        return;
    }
    if (![messageModel.type isEqualToString:@"R"]) {
        //消息分发
        NEEduSignalMessage *messageModel = [NEEduSignalMessage yy_modelWithJSON:message];
        [self dispatchMessage:messageModel];
        return;
    }
    /**
     1.根据sequence是否连续，判断消息是否丢失
     是：
     请求拉取丢失消息，分发
     否:
     更新当前sequence
     type = @'RM'不管sequence
     */
    if (messageModel.sequence - self.currentSequence > 1 && messageModel.sequence - self.currentSequence < 5) {
        //请求丢失的消息
        NSLog(@"sequence不连续 currentS:%d msgS:%d",self.currentSequence,messageModel.sequence);
        [HttpManager getMessageWithRoomUuid:self.profileInfo.snapshot.room.roomUuid nextId:self.currentSequence + 1 classType:[NEEduLostMessages class] success:^(NEEduLostMessages *  _Nonnull objModel) {
            NEEduSignalMessage *lastMessage = objModel.list.lastObject;
            self.currentSequence = lastMessage.sequence;
            for (NEEduSignalMessage *messageModel in objModel.list) {
                NSLog(@"sequence不连续 重新拉取%d",messageModel.sequence);
                [self dispatchMessage:messageModel];
            }
        } failure:^(NSError * _Nullable error, NSInteger statusCode) {
            NSLog(@"sequence不连续 重新拉取失败:%error",error);
        }];
    }else {
        //消息分发
        NEEduSignalMessage *messageModel = [NEEduSignalMessage yy_modelWithJSON:message];
        self.currentSequence = messageModel.sequence;
        [self dispatchMessage:messageModel];
    }
}

- (void)dispatchMessage:(NEEduSignalMessage *)messageModel {
    switch (messageModel.cmd) {
        case 1:
        {
            NEEduHttpRoom *lessonModel = [NEEduHttpRoom yy_modelWithJSON:messageModel.data];
            if (lessonModel.states.step) {
                //上下课状态更改
                self.profileInfo.snapshot.room.states.step = lessonModel.states.step;
                if (self.delegate && [self.delegate respondsToSelector:@selector(onLessonStateChange:roomUuid:)]) {
                    [self.delegate onLessonStateChange:lessonModel.states.step roomUuid:lessonModel.roomUuid];
                }
            }
            if (lessonModel.states.muteAudio) {
                self.profileInfo.snapshot.room.states.muteAudio = lessonModel.states.muteAudio;
                if (self.delegate && [self.delegate respondsToSelector:@selector(onLessonMuteAllAudio:roomUuid:)]) {
                    [self.delegate onLessonMuteAllAudio:lessonModel.states.muteAudio.value roomUuid:lessonModel.roomUuid];
                }
            }
            if (lessonModel.states.muteChat) {
                self.profileInfo.snapshot.room.states.muteChat = lessonModel.states.muteChat;
                if (self.delegate && [self.delegate respondsToSelector:@selector(onLessonMuteAllText:roomUuid:)]) {
                    [self.delegate onLessonMuteAllText:lessonModel.states.muteChat.value roomUuid:lessonModel.roomUuid];
                }
            }
        }
            break;
        case 30:
        {
            //用户加入
            //更新 profile
            NEEduSignalUserIn *userIn = [NEEduSignalUserIn yy_modelWithJSON:messageModel.data];
            NSMutableArray *temMembers = [NSMutableArray arrayWithArray:self.profileInfo.snapshot.members];
            NEEduHttpUser *newUser = userIn.members.firstObject;
            BOOL exist = NO;
            for (int i = 0; i < temMembers.count; i ++) {
                NEEduHttpUser *tempUser = temMembers[i];
                if ([tempUser.userUuid isEqualToString:newUser.userUuid]) {
                    [temMembers replaceObjectAtIndex:i withObject:newUser];
                    exist = YES;
                }
            }
            if (!exist) {
                if ([newUser.role isEqualToString:NEEduRoleHost]) {
                    [temMembers insertObject:newUser atIndex:0];
                }else {
                    [temMembers addObject:newUser];
                }
            }
            self.profileInfo.snapshot.members = temMembers;
            if (self.delegate && [self.delegate respondsToSelector:@selector(onUserInWithUser:members:)]) {
                [self.delegate onUserInWithUser:newUser members:temMembers.copy];
            }
        }
            break;
        case 31:
        {
            //用户离开
            NEEduSignalUserIn *userIn = [NEEduSignalUserIn yy_modelWithJSON:messageModel.data];
            NEEduHttpUser *newUser = userIn.members.firstObject;
            NSMutableArray *temMembers = [NSMutableArray arrayWithArray:self.profileInfo.snapshot.members];
            NEEduHttpUser *removeUser;
            for (int i = 0; i < temMembers.count; i ++) {
                NEEduHttpUser *tempUser = temMembers[i];
                if ([tempUser.userUuid isEqualToString:newUser.userUuid]) {
                    removeUser = tempUser;
                    break;
                }
            }
            if (removeUser) {
                [temMembers removeObject:removeUser];
            }
            if (self.delegate && [self.delegate respondsToSelector:@selector(onUserOutWithUser:members:)]) {
                [self.delegate onUserOutWithUser:newUser members:temMembers];
            }
        }
            break;
        case 40:
        {
            //用户开关音视频
            NEEduSignalStreamOnOff *streamOnOff = [NEEduSignalStreamOnOff yy_modelWithJSON:messageModel.data];
            NSMutableArray *temMembers = [NSMutableArray arrayWithArray:self.profileInfo.snapshot.members];
            NEEduHttpUser *tempUser;
            for (int i = 0; i < temMembers.count; i ++) {
                tempUser = temMembers[i];
                if ([tempUser.userUuid isEqualToString:streamOnOff.member.userUuid]) {
                    if (streamOnOff.streams.video) {
                        tempUser.streams.video = streamOnOff.streams.video;
                        if (self.delegate && [self.delegate respondsToSelector:@selector(onVideoStreamEnable:user:)]) {
                            [self.delegate onVideoStreamEnable:streamOnOff.streams.video.value user:tempUser];
                        }
                    }else if(streamOnOff.streams.audio) {
                        tempUser.streams.audio = streamOnOff.streams.audio;
                        if (self.delegate && [self.delegate respondsToSelector:@selector(onAudioStreamEnable:user:)]) {
                            [self.delegate onAudioStreamEnable:streamOnOff.streams.audio.value user:tempUser];
                        }
                    }else if(streamOnOff.streams.subVideo) {
                        tempUser.streams.subVideo = streamOnOff.streams.subVideo;
                        if (self.delegate && [self.delegate respondsToSelector:@selector(onSubVideoStreamEnable:user:)]) {
                            [self.delegate onSubVideoStreamEnable:streamOnOff.streams.subVideo.value user:tempUser];
                        }
                    }
                }
            }
        }
            break;
        case 41:
        {
            //用户开关音视频
            NEEduSignalStreamOnOff *streamOnOff = [NEEduSignalStreamOnOff yy_modelWithJSON:messageModel.data];
            NSMutableArray *temMembers = [NSMutableArray arrayWithArray:self.profileInfo.snapshot.members];
            NEEduHttpUser *tempUser;
            for (int i = 0; i < temMembers.count; i ++) {
                tempUser = temMembers[i];
                if ([tempUser.userUuid isEqualToString:streamOnOff.member.userUuid]) {
                    if ([streamOnOff.streamType isEqualToString:@"video"]) {
                        tempUser.streams.video.value = NO;
                        if (self.delegate && [self.delegate respondsToSelector:@selector(onVideoStreamEnable:user:)]) {
                            [self.delegate onVideoStreamEnable:tempUser.streams.video.value user:tempUser];
                        }
                    }else if([streamOnOff.streamType isEqualToString:@"audio"]) {
                        tempUser.streams.audio.value = NO;
                        if (self.delegate && [self.delegate respondsToSelector:@selector(onAudioStreamEnable:user:)]) {
                            [self.delegate onAudioStreamEnable:tempUser.streams.audio.value user:tempUser];
                        }
                    }else if([streamOnOff.streamType isEqualToString:@"subVideo"]) {
                        tempUser.streams.subVideo.value = NO;
                        if (self.delegate && [self.delegate respondsToSelector:@selector(onSubVideoStreamEnable:user:)]) {
                            [self.delegate onSubVideoStreamEnable:tempUser.streams.subVideo.value user:tempUser];
                        }
                    }
                }
            }
        }
            break;
        case 20:
        {
            NEEduSignalProperty *property = [NEEduSignalProperty yy_modelWithJSON:messageModel.data];
            NSMutableArray *temMembers = [NSMutableArray arrayWithArray:self.profileInfo.snapshot.members];
            NEEduHttpUser *tempUser;
            for (int i = 0; i < temMembers.count; i ++) {
                tempUser = temMembers[i];
                if ([tempUser.userUuid isEqualToString:property.member.userUuid]) {
                    if (property.properties.streamAV.video != nil) {
                        //更新自己
                        if ([self.localUser.userUuid isEqualToString:property.member.userUuid]) {
                            self.localUser.properties.streamAV.video = property.properties.streamAV.video;
                        }
                        tempUser.properties.streamAV = property.properties.streamAV;
                        if (self.delegate && [self.delegate respondsToSelector:@selector(onVideoAuthorizationEnable:user:)]) {
                            [self.delegate onVideoAuthorizationEnable:tempUser.properties.streamAV.video.intValue user:tempUser];
                        }
                    }
                    if (property.properties.streamAV.audio != nil) {
                        //更新自己
                        if ([self.localUser.userUuid isEqualToString:property.member.userUuid]) {
                            self.localUser.properties.streamAV.audio = property.properties.streamAV.audio;
                        }
                        tempUser.properties.streamAV = property.properties.streamAV;
                        if (self.delegate && [self.delegate respondsToSelector:@selector(onAudioAuthorizationEnable:user:)]) {
                            [self.delegate onAudioAuthorizationEnable:tempUser.properties.streamAV.audio.intValue user:tempUser];
                        }
                    }
                    if (property.properties.screenShare) {
                        //更新自己
                        if ([self.localUser.userUuid isEqualToString:property.member.userUuid]) {
                            self.localUser.properties.screenShare = property.properties.screenShare;
                        }
                        tempUser.properties.screenShare = property.properties.screenShare;
                        if (self.delegate && [self.delegate respondsToSelector:@selector(onScreenShareAuthorizationEnable:user:)]) {
                            [self.delegate onScreenShareAuthorizationEnable:tempUser.properties.screenShare.value user:tempUser];
                        }
                    }
                    if (property.properties.whiteboard) {
                        //更新自己
                        if ([self.localUser.userUuid isEqualToString:property.member.userUuid]) {
                            self.localUser.properties.whiteboard = property.properties.whiteboard;
                        }
                        NEEduWhiteboardProperty *item = [[NEEduWhiteboardProperty alloc] init];
                        item.drawable = property.properties.whiteboard.drawable;
                        tempUser.properties.whiteboard = item;
                        
                        if (self.delegate && [self.delegate respondsToSelector:@selector(onWhiteboardAuthorizationEnable:user:)]) {
                            [self.delegate onWhiteboardAuthorizationEnable:tempUser.properties.whiteboard.drawable user:tempUser];
                        }
                    }
                    if (property.properties.avHandsUp) {
                        //更新自己
                        if ([self.localUser.userUuid isEqualToString:property.member.userUuid]) {
                            self.localUser.properties.avHandsUp = property.properties.avHandsUp;
                        }
                        tempUser.properties.avHandsUp = property.properties.avHandsUp;
                        if (self.delegate && [self.delegate respondsToSelector:@selector(onHandsupStateChange:user:)]) {
                            [self.delegate onHandsupStateChange:tempUser.properties.avHandsUp.value user:tempUser];
                        }
                    }
                    break;
                }
           }
        }
            break;
        case 21:
        {
            NEEduRemoveProperty *remove = [NEEduRemoveProperty yy_modelWithDictionary:messageModel.data];
            if ([remove.key isEqualToString:@"screenShare"]) {
                for (NEEduHttpUser *user in self.profileInfo.snapshot.members) {
                    if ([user.userUuid isEqualToString:remove.member.userUuid]) {
                        user.properties.screenShare.value = 0;
                        if (self.delegate && [self.delegate respondsToSelector:@selector(onScreenShareAuthorizationEnable:user:)]) {
                            [self.delegate onScreenShareAuthorizationEnable:user.properties.screenShare.value user:user];
                        }
                        break;
                    }
                }
            }
            if ([remove.key isEqualToString:@"whiteboard"]) {
                for (NEEduHttpUser *user in self.profileInfo.snapshot.members) {
                    if ([user.userUuid isEqualToString:remove.member.userUuid]) {
                        user.properties.whiteboard.drawable = 0;
                        if (self.delegate && [self.delegate respondsToSelector:@selector(onWhiteboardAuthorizationEnable:user:)]) {
                            [self.delegate onWhiteboardAuthorizationEnable:user.properties.whiteboard.drawable user:user];
                        }
                        break;
                    }
                }
            }
//            if ([remove.key isEqualToString:@"avHandsUp"]) {
//                for (NEEduHttpUser *user in self.profileInfo.snapshot.members) {
//                    if ([user.userUuid isEqualToString:remove.member.userUuid]) {
//                        user.properties.screenShare.value = 0;
//                        if (self.delegate && [self.delegate respondsToSelector:@selector(onScreenShareAuthorizationEnable:user:)]) {
//                            [self.delegate onScreenShareAuthorizationEnable:tempUser.properties.screenShare.value user:tempUser];
//                        }
//                        break;
//                    }
//                }
//            }
        }
            break;
            
        default:
            break;
    }
}

@end

//
//  NEEduIMService.m
//  EduLogic
//
//  Created by Groot on 2021/5/14.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduIMService.h"
#import "NEEduIMCustomDecoder.h"
@interface NEEduIMService ()<NIMPassThroughManagerDelegate,NIMChatroomManagerDelegate,NIMChatManagerDelegate>
@property (nonatomic, strong ,readwrite) NIMChatroom *chatRoom;
@property (nonatomic, strong ,readwrite) NSMutableArray *chatMessages;
@end

@implementation NEEduIMService
- (void)setupAppkey:(NSString *)appKey {
    NIMSDKOption *option = [NIMSDKOption optionWithAppKey:appKey];
    [[NIMSDK sharedSDK] registerWithOption:option];
    [self registerCustomDecoder:[NEEduIMCustomDecoder new]];
}

- (void)addIMDelegate {
    [[NIMSDK sharedSDK].passThroughManager addDelegate:self];
    [[NIMSDK sharedSDK].chatroomManager addDelegate:self];
    [[NIMSDK sharedSDK].chatManager addDelegate:self];
}
- (void)registerCustomDecoder:(id<NIMCustomAttachmentCoding>)decoder {
    [NIMCustomObject registerCustomDecoder:decoder];
}
- (BOOL)isLogined {
    return [NIMSDK sharedSDK].loginManager.isLogined;
}
- (void)login:(NSString *)userID token:(NSString *)token completion:(void(^)(NSError * _Nullable error))completion {
    [[NIMSDK sharedSDK].loginManager login:userID token:token completion:^(NSError * _Nullable error) {
        if (completion) {
            completion(error);
        }
    }];
}
- (void)logoutWithCompletion:(nullable void(^)(NSError * _Nullable error))completion {
    [[NIMSDK sharedSDK].loginManager logout:completion];
}
- (void)enterChatRoomWithParam:(NEEduChatRoomParam *)param success:(void(^)(NEEduChatRoomResponse *response))success failed:(void(^)(NSError *error))failed {
    NIMChatroomEnterRequest *request = [[NIMChatroomEnterRequest alloc] init];
    request.roomId = param.chatRoomID;
    request.roomNickname = param.nickname;
    request.roomAvatar = param.avatar;
    __weak typeof(self) weakSelf = self;
    [[NIMSDK sharedSDK].chatroomManager enterChatroom:request completion:^(NSError * _Nullable error, NIMChatroom * _Nullable chatroom, NIMChatroomMember * _Nullable member) {
        __weak typeof(self) strongSelf = weakSelf;
        strongSelf.chatRoom = chatroom;
        if (error) {
            if (failed) {
                failed(error);
            }
        }else {
            if (success) {
                NEEduChatRoomResponse *res = [[NEEduChatRoomResponse alloc] init];
                res.chatRoomID = chatroom.roomId;
                res.chatRoomName = chatroom.name;
                res.userID = member.userId;
                res.userNickName = member.roomNickname;
                res.avatar = member.roomAvatar;
                success(res);
            }
        }
    }];
}
- (void)exitChatroom:(NSString *)roomId
          completion:(nullable void(^)(NSError *))completion {
    [[NIMSDK sharedSDK].chatroomManager exitChatroom:roomId completion:^(NSError * _Nullable error) {
        if (completion) {
            completion(error);
        }
    }];
}

- (void)fetchChatroomInfo:(void(^)(NSError *error,NEEduChatRoomInfo *chatRoom))completion {
    [[NIMSDK sharedSDK].chatroomManager fetchChatroomInfo:self.chatRoom.roomId completion:^(NSError * _Nullable error, NIMChatroom * _Nullable chatroom) {
        if (completion) {
            NEEduChatRoomInfo *chatRoom = [[NEEduChatRoomInfo alloc] init];
            chatRoom.roomId = chatroom.roomId;
            chatRoom.name = chatroom.name;
            chatRoom.onlineNumber = chatroom.onlineUserCount;
            completion(error,chatRoom);
        }
    }];
}

- (void)getChatroomMembers:(NSString *)roomId result:(void(^)(NSError *error,NSArray<NIMChatroomMember *> * _Nullable members))result {
    NIMChatroomMemberRequest *request = [[NIMChatroomMemberRequest alloc] init];
    request.roomId = roomId;
    request.limit = 2000;
    request.type = NIMChatroomFetchMemberTypeTemp;
    [[NIMSDK sharedSDK].chatroomManager fetchChatroomMembers:request completion:^(NSError * _Nullable error, NSArray<NIMChatroomMember *> * _Nullable members) {
        result(error,members);
    }];
}

- (void)sendChatroomTextMessage:(NSString *)text error:(NSError * __nullable *)error {
    if (!self.chatRoom) {
        return;
    }
    NIMMessage *textMessage = [[NIMMessage alloc] init];
    textMessage.text = text;
    NIMSession *session = [NIMSession session:self.chatRoom.roomId type:NIMSessionTypeChatroom];
    [[NIMSDK sharedSDK].chatManager sendMessage:textMessage toSession:session error:error];
}

- (void)sendChatroomImageMessage:(UIImage *)image error:(NSError * __nullable *)error {
    if (!self.chatRoom) {
        return;
    }
    NIMMessage *imageMessage = [[NIMMessage alloc] init];
    imageMessage.messageObject = [[NIMImageObject alloc] initWithImage:image];
    NIMSession *session = [NIMSession session:self.chatRoom.roomId type:NIMSessionTypeChatroom];
    BOOL result = [[NIMSDK sharedSDK].chatManager sendMessage:imageMessage toSession:session error:error];
    NSLog(@"result:%d",result);
}

- (void)resendMessage:(NIMMessage *)message error:(NSError * __nullable *)error {
    BOOL result = [[NIMSDK sharedSDK].chatManager resendMessage:message error:error];
    NSLog(@"resendMessage result:%d",result);
}

#pragma mark - NIMPassThroughManagerDelegate
- (void)didReceivedPassThroughMsg:(NIMPassThroughMsgData *)recvData {
    if (self.delegate && [self.delegate respondsToSelector:@selector(didRecieveSignalMessage:fromUser:)]) {
        [self.delegate didRecieveSignalMessage:recvData.body fromUser:recvData.fromAccid];
    }
}

#pragma mark - NIMChatManagerDelegate
- (void)onRecvMessages:(NSArray<NIMMessage *> *)messages {
    if (self.chatDelegate && [self.chatDelegate respondsToSelector:@selector(didRecieveChatMessages:)]) {
        [self.chatDelegate didRecieveChatMessages:messages];
    }
}
- (void)willSendMessage:(NIMMessage *)message {
    if (self.chatDelegate && [self.chatDelegate respondsToSelector:@selector(willSendMessage:)]) {
        [self.chatDelegate willSendMessage:message];
    }
}
- (void)sendMessage:(NIMMessage *)message progress:(float)progress {
    if (self.chatDelegate && [self.chatDelegate respondsToSelector:@selector(sendMessage:progress:)]) {
        [self.chatDelegate sendMessage:message progress:progress];
    }
}
- (void)sendMessage:(NIMMessage *)message didCompleteWithError:(nullable NSError *)error {
    if (self.chatDelegate && [self.chatDelegate respondsToSelector:@selector(didSendMessage:error:)]) {
        [self.chatDelegate didSendMessage:message error:error];
    }
}

#pragma mark - NIMChatroomManagerDelegate
- (void)chatroomBeKicked:(NIMChatroomBeKickedResult *)result {
    NSLog(@"[IM Chatroom] %s",__func__);
}
- (void)chatroom:(NSString *)roomId autoLoginFailed:(NSError *)error {
    NSLog(@"[IM Chatroom] %s",__func__);
}
- (void)chatroom:(NSString *)roomId connectionStateChanged:(NIMChatroomConnectionState)state {
    NSLog(@"[IM Chatroom] %s  state:%d",__func__,state);
}

- (void)leaveChatRoom {
    if (self.chatRoom) {
        [self exitChatroom:self.chatRoom.roomId completion:nil];
        self.chatRoom = nil;
    }
    self.chatMessages = nil;
}

- (void)logout {
    if ([NIMSDK sharedSDK].loginManager.isLogined) {
        [self logoutWithCompletion:nil];
    }
}
@end

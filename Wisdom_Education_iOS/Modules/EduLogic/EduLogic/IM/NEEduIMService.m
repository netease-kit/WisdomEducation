//
//  NEEduIMService.m
//  EduLogic
//
//  Created by Groot on 2021/5/14.
//

#import "NEEduIMService.h"
@interface NEEduIMService ()<NIMPassThroughManagerDelegate,NIMChatroomManagerDelegate,NIMChatManagerDelegate>
@property (nonatomic, strong ,readwrite) NIMChatroom *chatRoom;
//@property (nonatomic, strong) NIMChatroom *chatRoom;

@property (nonatomic, strong ,readwrite) NSMutableArray *chatMessages;
@end

@implementation NEEduIMService
- (void)setupAppkey:(NSString *)appKey {
    NIMSDKOption *option = [NIMSDKOption optionWithAppKey:appKey];
    [[NIMSDK sharedSDK] registerWithOption:option];
    [[NIMSDK sharedSDK].passThroughManager addDelegate:self];
    [[NIMSDK sharedSDK].chatroomManager addDelegate:self];
    [[NIMSDK sharedSDK].chatManager addDelegate:self];
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

- (void)sendChatroomTextMessage:(NSString *)text error:(NSError * __nullable *)error {
    if (!self.chatRoom) {
        return;
    }
    NIMMessage *textMessage = [[NIMMessage alloc] init];
    textMessage.text = text;
    NIMSession *session = [NIMSession session:self.chatRoom.roomId type:NIMSessionTypeChatroom];
    [[NIMSDK sharedSDK].chatManager sendMessage:textMessage toSession:session error:error];
}

#pragma mark - NIMPassThroughManagerDelegate
- (void)didReceivedPassThroughMsg:(NIMPassThroughMsgData *)recvData {
    if (self.delegate && [self.delegate respondsToSelector:@selector(didRecieveSignalMessage:fromUser:)]) {
        [self.delegate didRecieveSignalMessage:recvData.body fromUser:recvData.fromAccid];
    }
}

#pragma mark - NIMChatManagerDelegate
- (void)onRecvMessages:(NSArray<NIMMessage *> *)messages {
    [self.chatMessages addObjectsFromArray:messages];
    if (self.chatDelegate && [self.chatDelegate respondsToSelector:@selector(didRecieveChatMessages:)]) {
        [self.chatDelegate didRecieveChatMessages:messages];
    }
}

- (void)sendMessage:(NIMMessage *)message didCompleteWithError:(nullable NSError *)error {
    if (!error) {
        [self.chatMessages addObject:message];
    }
    if (self.chatDelegate && [self.chatDelegate respondsToSelector:@selector(didSendMessage:error:)]) {
        [self.chatDelegate didSendMessage:message error:error];
    }
}

#pragma mark - NIMChatroomManagerDelegate
- (void)chatroomBeKicked:(NIMChatroomBeKickedResult *)result {
    YXAlogError(@"[IM Chatroom] %s",__func__);
}
- (void)chatroom:(NSString *)roomId autoLoginFailed:(NSError *)error {
    YXAlogError(@"[IM Chatroom] %s",__func__);
}
- (void)chatroom:(NSString *)roomId connectionStateChanged:(NIMChatroomConnectionState)state {
    YXAlogInfo(@"[IM Chatroom] %s  state:%d",__func__,state);
}

- (NSMutableArray *)chatMessages {
    if (!_chatMessages) {
        _chatMessages = [NSMutableArray array];
    }
    return _chatMessages;
}
- (void)destroy {
    if ([NIMSDK sharedSDK].loginManager.isLogined) {
        [self logoutWithCompletion:nil];
    }
    if (self.chatRoom) {
        [self exitChatroom:self.chatRoom.roomId completion:nil];
        self.chatRoom = nil;
    }
    self.chatMessages = nil;
}
@end

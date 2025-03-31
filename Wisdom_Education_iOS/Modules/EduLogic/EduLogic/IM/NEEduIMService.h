//
//  NEEduIMService.h
//  EduLogic
//
//  Created by Groot on 2021/5/14.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NEEduChatRoomParam.h"
#import "NEEduChatRoomResponse.h"
#import "NEEduChatRoomInfo.h"
#import <NIMSDK/NIMSDK.h>

NS_ASSUME_NONNULL_BEGIN
@protocol NEEduIMServiceDelegate <NSObject>
- (void)didRecieveSignalMessage:(NSString *)message fromUser:(NSString *)fromUser;
@end

@protocol NEEduIMChatDelegate <NSObject>
@optional
/// 将要发送消息回调
/// @param message 消息
- (void)willSendMessage:(NIMMessage *)message;

/// 消息发送进度回调
/// @param message 消息
/// @param progress 进度
- (void)sendMessage:(NIMMessage *)message progress:(float)progress;

/// 消息发送完成回调
/// @param message 消息
/// @param error 错误
- (void)didSendMessage:(NIMMessage *)message error:(NSError *)error;

/// 收到消息回调
/// @param messages 消息
- (void)didRecieveChatMessages:(NSArray <NIMMessage *>*)messages;

@end

@interface NEEduIMService : NSObject

@property (nonatomic, weak) id<NEEduIMServiceDelegate> delegate;
@property (nonatomic, weak) id<NEEduIMChatDelegate> chatDelegate;
@property (nonatomic, strong ,readonly) NSMutableArray *chatMessages;
@property (nonatomic, strong ,readonly) NIMChatroom *chatRoom;
@property (nonatomic, assign ,readonly) BOOL isLogined;

- (void)setupAppkey:(NSString *)appKey;

/// 注册自定义消息解析类
/// @param decoder decoder 解析类
- (void)registerCustomDecoder:(id<NIMCustomAttachmentCoding>)decoder;

- (void)addIMDelegate;

- (void)login:(NSString *)userID token:(NSString *)token completion:(void(^)(NSError * _Nullable error))completion;

- (void)logoutWithCompletion:(nullable void(^)(NSError * _Nullable error))completion;

- (void)enterChatRoomWithParam:(NEEduChatRoomParam *)param success:(void(^)(NEEduChatRoomResponse *response))success failed:(void(^)(NSError *error))failed;

- (void)exitChatroom:(NSString *)roomId completion:(nullable void(^)(NSError *))completion;

- (void)sendChatroomTextMessage:(NSString *)text error:(NSError * __nullable *)error;
- (void)sendChatroomImageMessage:(UIImage *)image error:(NSError * __nullable *)error;
- (void)resendMessage:(NIMMessage *)message error:(NSError * __nullable *)error;
- (void)fetchChatroomInfo:(void(^)(NSError *error,NEEduChatRoomInfo *chatRoom))completion;

/// 请求聊天成员 结果按照加入聊天室时间戳倒序返回
/// @param member 查询在该用户之前加入的用户列表
/// @param result 返回用户列表
- (void)getMembersFromMember:(NIMChatroomMember *)member result:(void(^)(NSError *error,NSArray<NIMChatroomMember *> * _Nullable members))result;
/// 离开聊天室
- (void)leaveChatRoom;
/// 登出IM
- (void)logout;

@end

NS_ASSUME_NONNULL_END

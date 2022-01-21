//
//  NEEduVideoService.h
//  EduLogic
//
//  Created by Groot on 2021/5/14.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import <Foundation/Foundation.h>
#import "NERtcVideoCanvasExtention.h"
#import "NEEduRtcJoinChannelParam.h"

NS_ASSUME_NONNULL_BEGIN

@protocol NEEduVideoServiceDelegate <NSObject>
@optional
- (void)onUserDidJoinWithUserID:(UInt64)userID;
- (void)onNetQuality:(NERtcNetworkQualityStats *)quality;
- (void)onRtcDisconnectWithReason:(NERtcError *)reason;
- (void)onSubStreamDidStop:(UInt64)userID;
@end


@interface NEEduRtcService : NSObject
@property (nonatomic, weak) id <NEEduVideoServiceDelegate>delegate;
///在subscribeVideo为NO的情况下，缓存需要订阅的用户，解决外部收到用户加入的回调时，用户还未加入Rtc房间，手动订阅失败的问题（实则是App服务器的bug，应该收到用户加入和Rtc用户加入后，才能通知客户端）。
@property (nonatomic, copy) NSIndexSet *subscribeCacheList;
- (void)setupAppkey:(NSString *)appKey isConfigRead:(BOOL)isConfigRead;
- (void)joinChannel:(NEEduRtcJoinChannelParam *)param completion:(void(^)(NSError *error,uint64_t channelID))completion;
//video canvas
- (int)setupLocalVideo:(NERtcVideoCanvasExtention * _Nullable)local;
- (int)setupRemoteVideo:(NERtcVideoCanvasExtention * _Nonnull)remote;
//SubStream Render
- (int)setupSubStreamVideo:(NERtcVideoCanvasExtention * _Nonnull)remote;

// media on-off
- (int)enableLocalAudio:(BOOL)enable;
- (int)enableLocalVideo:(BOOL)enable;
- (int)muteLocalVideo:(BOOL)mute;
- (int)muteLocalAudio:(BOOL)mute;

- (int)subscribeVideo:(BOOL)subscribe forUserID:(UInt64)userID;
- (int)subscribeAudio:(BOOL)subscribe forUserID:(UInt64)userID;

- (void)leaveChannel;
- (void)destroy;

@end



NS_ASSUME_NONNULL_END

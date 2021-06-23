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
@end


@interface NEEduVideoService : NSObject
@property (nonatomic, weak) id <NEEduVideoServiceDelegate>delegate;

- (void)setupAppkey:(NSString *)appKey;
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

- (void)subscribeVideo:(BOOL)subscribe forUserID:(UInt64)userID;
- (void)subscribeAudio:(BOOL)subscribe forUserID:(UInt64)userID;

- (void)leaveChannel;
- (void)destroy;
@end



NS_ASSUME_NONNULL_END

//
//  NEEduVideoService.m
//  EduLogic
//
//  Created by Groot on 2021/5/14.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduRtcService.h"
#import <NERtcSDK/NERtcSDK.h>
#import <YYModel/YYModel.h>
@interface NEEduRtcService ()<NERtcEngineDelegateEx,NERtcEngineMediaStatsObserver>
@property (nonatomic, assign) BOOL subscribeVideo;
@property (nonatomic, assign) BOOL subscribeAideo;

- (void)setupAppkey:(NSString *)appKey;
@end

@implementation NEEduRtcService

- (void)setupAppkey:(NSString *)appKey isConfigRead:(BOOL)isConfigRead {
    /// 进入房间前 设置场景
    [[NERtcEngine sharedEngine] setChannelProfile:kNERtcChannelProfileCommunication];
    // 设置视频发送配置(帧率/分辨率)
    NERtcVideoEncodeConfiguration *config = [[NERtcVideoEncodeConfiguration alloc] init];
    config.width = 320;
    config.height = 240;
    config.frameRate = kNERtcVideoFrameRateFps15;
    config.degradationPreference = kNERtcDegradationMaintainFramerate;
    [[NERtcEngine sharedEngine] setLocalVideoConfig:config];
    // 设置音频质量&场景
    [[NERtcEngine sharedEngine] setAudioProfile:kNERtcAudioProfileStandard scenario:kNERtcAudioScenarioSpeech];
    NERtcEngineContext *context = [[NERtcEngineContext alloc] init];
    context.engineDelegate = self;
    context.appKey = appKey;
    if (isConfigRead) {
        NSString *path = [NSBundle.mainBundle pathForResource:@"rtc_server" ofType:@"conf"];
        NSData *data = [NSData dataWithContentsOfFile:path];
        if (data) {
            NSError *error = nil;
            NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:data options:0 error:&error];
            if (!error) {
                context.serverAddress = [NERtcServerAddresses yy_modelWithJSON:dict];
            }
        }
    }
    [[NERtcEngine sharedEngine] setupEngineWithContext:context];
    [[NERtcEngine sharedEngine] setExternalVideoSource:YES isScreen:YES];
    [[NERtcEngine sharedEngine] addEngineMediaStatsObserver:self];
}
- (void)joinChannel:(NEEduRtcJoinChannelParam *)param completion:(void(^)(NSError *error,uint64_t channelID))completion {
    self.subscribeVideo = param.subscribeVideo;
    self.subscribeAideo = param.subscribeAudio;
    NSDictionary *params = @{
        kNERtcKeyAutoSubscribeAudio: @(param.subscribeAudio),
    };
    [[NERtcEngine sharedEngine] setParameters:params];
    [[NERtcEngine sharedEngine] enableLocalAudio:NO];
    [[NERtcEngine sharedEngine] enableLocalVideo:NO];
    [[NERtcEngine sharedEngine] joinChannelWithToken:param.rtcToken channelName:param.channelID myUid:param.userID completion:^(NSError * _Nullable error, uint64_t channelId, uint64_t elapesd, uint64_t uid) {
        NSLog(@"Rtc:joinError:%@",error);
        if (completion) {
            completion(error,channelId);
        }
    }];
}
- (int)subscribeVideo:(BOOL)subscribe forUserID:(UInt64)userID {
    int code = [[NERtcEngine sharedEngine] subscribeRemoteVideo:subscribe forUserID:userID streamType:kNERtcRemoteVideoStreamTypeLow];
    if (code != 0) {
        NSLog(@"[Rtc]subscribeVideo:%d userId:%ud code:%d",subscribe,userID,code);
    }
    return code;
}
- (int)subscribeAudio:(BOOL)subscribe forUserID:(UInt64)userID {
    return [[NERtcEngine sharedEngine] subscribeRemoteAudio:subscribe forUserID:userID];
}
- (int)setupLocalVideo:(NERtcVideoCanvasExtention *)local
{
    return [[NERtcEngine sharedEngine] setupLocalVideoCanvas:local];
}

- (int)setupRemoteVideo:(NERtcVideoCanvasExtention * _Nonnull)remote
{
    return [[NERtcEngine sharedEngine] setupRemoteVideoCanvas:remote forUserID:remote.uid];
}
- (int)setupSubStreamVideo:(NERtcVideoCanvasExtention * _Nonnull)remote
{
    return [[NERtcEngine sharedEngine] setupRemoteSubStreamVideoCanvas:remote forUserID:remote.uid];
}


- (int)enableLocalAudio:(BOOL)enable
{
    return [[NERtcEngine sharedEngine] enableLocalAudio:enable];
}

- (int)enableLocalVideo:(BOOL)enable
{
    return [[NERtcEngine sharedEngine] enableLocalVideo:enable];
}

- (int)muteLocalVideo:(BOOL)mute
{
    return [[NERtcEngine sharedEngine] muteLocalVideo:mute];
}

- (int)muteLocalAudio:(BOOL)mute
{
    return [[NERtcEngine sharedEngine] muteLocalAudio:mute];
}

- (void)leaveChannel {
    [[NERtcEngine sharedEngine] leaveChannel];
}

- (void)destroy {
    [NERtcEngine destroyEngine];
}
#pragma mark -delegate
- (void)onNERtcEngineUserDidJoinWithUserID:(uint64_t)userID userName:(NSString *)userName
{
    if (self.delegate &&[self.delegate respondsToSelector:@selector(onUserDidJoinWithUserID:)]) {
        [self.delegate onUserDidJoinWithUserID:userID];
    }
    
}
- (void)onNERtcEngineUserVideoDidStartWithUserID:(uint64_t)userID videoProfile:(NERtcVideoProfileType)profile {
    if (self.subscribeVideo) {
        int code = [[NERtcEngine sharedEngine] subscribeRemoteVideo:YES forUserID:userID streamType:kNERtcRemoteVideoStreamTypeLow];
        NSLog(@"auto subscribeVideo code:%d",code);
    }else {
        if ([self.subscribeCacheList containsIndex:userID]) {
            int code = [[NERtcEngine sharedEngine] subscribeRemoteVideo:YES forUserID:userID streamType:kNERtcRemoteVideoStreamTypeLow];
            [[NERtcEngine sharedEngine] subscribeRemoteAudio:YES forUserID:userID];
            NSLog(@"subscribeVideo code:%d",code);
        }
    }
}
- (void)onNERtcEngineUserVideoDidStop:(uint64_t)userID {
    if (self.subscribeVideo) {
        [[NERtcEngine sharedEngine] subscribeRemoteVideo:NO forUserID:userID streamType:kNERtcRemoteVideoStreamTypeLow];
    }
}
// subStream
- (void)onNERtcEngineUserSubStreamDidStartWithUserID:(uint64_t)userID subStreamProfile:(NERtcVideoProfileType)profile {
    [[NERtcEngine sharedEngine] subscribeRemoteSubStreamVideo:YES forUserID:userID];
}
- (void)onNERtcEngineUserSubStreamDidStop:(uint64_t)userID {
    [[NERtcEngine sharedEngine] subscribeRemoteSubStreamVideo:NO forUserID:userID];
    if (self.delegate && [self.delegate respondsToSelector:@selector(onSubStreamDidStop:)]) {
        [self.delegate onSubStreamDidStop:userID];
    }
}

- (void)onNetworkQuality:(NSArray<NERtcNetworkQualityStats *> *)stats {
    for (NERtcNetworkQualityStats *state in stats) {
        if (self.delegate && [self.delegate respondsToSelector:@selector(onNetQuality:)]) {
            [self.delegate onNetQuality:state];
        }
    }
}
- (void)onNERtcEngineDidDisconnectWithReason:(NERtcError)reason {
    NSLog(@"RTC:%s reason:%d",__func__,reason);
    if (self.delegate && [self.delegate respondsToSelector:@selector(onRtcDisconnectWithReason:)]) {
        [self.delegate onRtcDisconnectWithReason:reason];
    }
}

- (void)onNERtcEngineDidError:(NERtcError)errCode {
    NSLog(@"RTC:%s errCode:%d",__func__,errCode);
}

-(void)onNERtcEngineConnectionStateChangeWithState:(NERtcConnectionStateType)state
                                             reason:(NERtcReasonConnectionChangedType)reason {
    NSLog(@"RTC:%s reason:%d",__func__,reason);

    
}
- (void)onNERtcEngineDidLeaveChannelWithResult:(NERtcError)result {
    NSLog(@"RTC:%s result:%d",__func__,result);
}

- (void)onNERtcEngineUserDidLeaveWithUserID:(uint64_t)userID reason:(NERtcSessionLeaveReason)reason {
    NSLog(@"RTC:%s reason:%d",__func__,reason);
}

- (void)onNERtcEngineReconnectingStart {
    NSLog(@"RTC:%s",__func__);
}

@end

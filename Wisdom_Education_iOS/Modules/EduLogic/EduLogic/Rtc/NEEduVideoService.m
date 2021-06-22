//
//  NEEduVideoService.m
//  EduLogic
//
//  Created by Groot on 2021/5/14.
//

#import "NEEduVideoService.h"
#import <NERtcSDK/NERtcSDK.h>

@interface NEEduVideoService ()<NERtcEngineDelegateEx,NERtcEngineMediaStatsObserver>
@property (nonatomic, assign) BOOL subscribeVideo;
@property (nonatomic, assign) BOOL subscribeAideo;

- (void)setupAppkey:(NSString *)appKey;
@end

@implementation NEEduVideoService

- (void)setupAppkey:(NSString *)appKey {
    [[NERtcEngine sharedEngine] setChannelProfile:kNERtcChannelProfileCommunication];
    // 设置视频发送配置(帧率/分辨率)
    NERtcVideoEncodeConfiguration *config = [[NERtcVideoEncodeConfiguration alloc] init];
    config.width = 320;
    config.height = 240;
    config.frameRate = kNERtcVideoFrameRateFps24;
    [[NERtcEngine sharedEngine] setLocalVideoConfig:config];
    // 设置音频质量&场景
    [[NERtcEngine sharedEngine] setAudioProfile:kNERtcAudioProfileStandard scenario:kNERtcAudioScenarioSpeech];
    NERtcEngineContext *context = [[NERtcEngineContext alloc] init];
    context.engineDelegate = self;
    context.appKey = appKey;
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
    
    [[NERtcEngine sharedEngine] joinChannelWithToken:param.rtcToken channelName:param.channelID myUid:param.userID completion:^(NSError * _Nullable error, uint64_t channelId, uint64_t elapesd) {
        if (completion) {
            completion(error,channelId);
        }
    }];
}
- (void)subscribeVideo:(BOOL)subscribe forUserID:(UInt64)userID {
    [[NERtcEngine sharedEngine] subscribeRemoteVideo:subscribe forUserID:userID streamType:kNERtcRemoteVideoStreamTypeLow];
}
- (void)subscribeAudio:(BOOL)subscribe forUserID:(UInt64)userID {
    [[NERtcEngine sharedEngine] subscribeRemoteAudio:subscribe forUserID:userID];
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
        [[NERtcEngine sharedEngine] subscribeRemoteVideo:YES forUserID:userID streamType:kNERtcRemoteVideoStreamTypeLow];
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
}

- (void)onNetworkQuality:(NSArray<NERtcNetworkQualityStats *> *)stats {
    for (NERtcNetworkQualityStats *state in stats) {
        if (self.delegate && [self.delegate respondsToSelector:@selector(onNetQuality:)]) {
            [self.delegate onNetQuality:state];
        }
    }
}
- (void)onNERtcEngineDidDisconnectWithReason:(NERtcError)reason {
    if (self.delegate && [self.delegate respondsToSelector:@selector(onRtcDisconnectWithReason:)]) {
        [self.delegate onRtcDisconnectWithReason:reason];
    }
}
@end

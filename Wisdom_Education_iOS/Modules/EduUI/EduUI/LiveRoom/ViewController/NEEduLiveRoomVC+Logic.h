// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
    

#import "NEEduLiveRoomVC.h"

NS_ASSUME_NONNULL_BEGIN
/// 直播大班课逻辑扩展
@interface NEEduLiveRoomVC (Logic)
/// 直播的快照
- (void)getLiveRoomSnapshot;
/// 互动的快照
- (void)getRTCRoomSnapshot;
/// 初始化播放器
- (void)initLivePlayer:(NSString *)urlString;
///更新菜单的音视频按钮
- (void)updateMyselfAVItemWithUser:(NEEduHttpUser *)user;
/// 添加/删除 通知
- (void)addNotificaton;
- (void)removeNotification;
///打开音视频、屏幕共享
- (void)turnOnAudio:(BOOL)isOn;
- (void)turnOnVideo:(BOOL)isOn;
- (void)turnOnShareScreen:(BOOL)isOn item:(NEEduMenuItem *)item;
///停止所有屏幕共享
- (void)stopAllScreenShare;
- (void)stopRecord;
- (void)stopScreenShare:(BOOL)isShow;
- (void)onScreenShareAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user;
///离开白板房间
- (void)leaveRoom;
@end

NS_ASSUME_NONNULL_END

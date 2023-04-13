// Copyright (c) 2022 NetEase, Inc. All rights reserved.
// Use of this source code is governed by a MIT license that can be
// found in the LICENSE file.
    

#import "NEEduLiveRoomVC+Logic.h"
#import "NEEduLiveRoomVC+UI.h"
#import "NEEduLiveRoomVC+Seat.h"
#import "NEEduLiveMembersVC.h"
#import <SDWebImage/SDWebImageDownloader.h>
#import "NSString+NE.h"
#import "UIView+NE.h"

static NSString * kLastRtcCid = @"lastRtcCid";
static NSString *kAppGroup = @"group.com.netease.yunxin.app.wisdom.education";
@interface NEEduLiveRoomVC (Logic) <NEEduIMChatDelegate,NMCWhiteboardManagerDelegate, NEEduRoomServiceDelegate,NEEduMessageServiceDelegate,NEScreenShareHostDelegate, NEEduVideoServiceDelegate>

@end

@implementation NEEduLiveRoomVC (Logic)
- (void)getLiveRoomSnapshot {
    //请求
    __weak typeof(self)weakSelf = self;
    NCKLogInfo(@"LiveRoom viewDidLoad");
    [[NEEduManager shared].roomService getRoomProfile:self.roomUuid
                                           completion:^(NSError * _Nonnull error, NEEduRoomProfile * _Nonnull profile) {
        if (error) {
            [self.view makeToast:[NSString stringWithFormat:@"加入房间失败，请退出重进 %@",error.localizedDescription]];
            return;
        }
        
        [[NSUserDefaults standardUserDefaults] setObject:profile.snapshot.room.rtcCid forKey:kLastRtcCid];
        weakSelf.profile = profile;
        weakSelf.muteChat = profile.snapshot.room.states.muteChat.value;
        if (profile.snapshot.room.states.step.value == NEEduLessonStateClassIn) {
            weakSelf.lessonStateView.hidden = YES;
            NSString *urlString = self.useFastLive ? profile.snapshot.room.properties.live.pullRtsUrl : profile.snapshot.room.properties.live.pullRtmpUrl;
            [weakSelf initLivePlayer:urlString];
            [weakSelf.player prepareToPlay];
            // add handsup item
//            [weakSelf addHandsUpMenue];
            [self.maskView insertItem:self.handsupItem atIndex:1];
        } else {
            weakSelf.lessonStateView.hidden = NO;
        }
//        [NEEduManager shared].imService.chatDelegate = weakSelf;
        [NEEduManager shared].roomService.delegate = weakSelf;
        [NEEduManager shared].messageService.delegate = weakSelf;
//        //get seat request lise
//        [weakSelf getSeatRequestList];
//        self.leaveState = NEEduLeaveSeatActive;//默认值
//        // get seat info
//        [weakSelf getSeatInfo];
        // update room info
        weakSelf.room = profile.snapshot.room;
        // navigation info
        [weakSelf.maskView.navView updateRoomState:weakSelf.room serverTime:profile.ts];
        // update members list
        for (NEEduHttpUser *user in profile.snapshot.members) {
            NIMChatroomMember *chatMember = [[NIMChatroomMember alloc] init];
            chatMember.roomNickname = user.userName;
            [weakSelf.members addObject:chatMember];
        }
        //占位老师
        weakSelf.totalMembers = [NSMutableArray arrayWithArray:[weakSelf placeholderMembers]];
        //weakSelf.members = profile.snapshot.members;
        //load chatroom function
        [weakSelf addChatroom:weakSelf.room];
    }];
}
- (void)getRTCRoomSnapshot {
    //请求
    __weak typeof(self)weakSelf = self;
    NCKLogInfo(@"rtcRoom viewDidLoad");
    //让视频方向跟随APP方向，在加入房间之前调用
    [[NERtcEngine sharedEngine]setVideoRotationMode:1];
    [NEEduManager shared].rtcService.delegate = self;
    //加入Rtc房间并获取快照
    [[NEEduManager shared] joinRtcAndGetProfileCompletion:^(NSError * _Nonnull error, NEEduRoomProfile * _Nonnull profile) {
        if (error) {
            [self.view makeToast:[NSString stringWithFormat:@"重新布局房间失败，请退出重进 %@",error.localizedDescription]];
            return;
        }
        [[NSUserDefaults standardUserDefaults] setObject:profile.snapshot.room.rtcCid forKey:kLastRtcCid];
        
        weakSelf.profile = profile;
        weakSelf.muteChat = profile.snapshot.room.states.muteChat.value;
        weakSelf.lessonStateView.hidden = YES;
        
        [NMCWhiteboardManager sharedManager].delegate = weakSelf;
        [NEEduManager shared].roomService.delegate = weakSelf;
        [NEEduManager shared].messageService.delegate = weakSelf;
        
        weakSelf.room = profile.snapshot.room;
        // update onlinemembers list
        weakSelf.totalMembers = [weakSelf showMembersWithJoinedMembers:profile.snapshot.members].mutableCopy;
        
        for (NEEduHttpUser *user in profile.snapshot.members) {
            if( user.streams.subVideo.value ){
                weakSelf.userIsShareScreen = true;
                weakSelf.shareScreenView.hidden = NO;
                [self onSubVideoStreamEnable:true user:user];
                break;
            }
        }
        if(!weakSelf.userIsShareScreen) {
            [weakSelf addWhiteboardView];
        }

        [weakSelf.maskView.navView updateRoomState:weakSelf.room serverTime:profile.ts];
//        [weakSelf addWhiteboardView];
        self.whiteboardWritable = NO;
        [[NMCWhiteboardManager sharedManager] callEnableDraw:self.whiteboardWritable];
        [[NMCWhiteboardManager sharedManager] hiddenTools:!self.whiteboardWritable];
        [weakSelf updateUIWithMembers:weakSelf.totalMembers];
        //load chatroom function
//        [weakSelf addChatroom:weakSelf.room];
        [weakSelf.collectionView reloadData];
    }];
    [[UIApplication sharedApplication] setIdleTimerDisabled:YES];
}
- (void)getleaveRTCRoomSnapshot {
    //请求
    __weak typeof(self)weakSelf = self;
    NCKLogInfo(@"rtcRoom viewDidLoad");
    [[NEEduManager shared].roomService getRoomProfile:self.roomUuid
                                           completion:^(NSError * _Nonnull error, NEEduRoomProfile * _Nonnull profile) {
        if (error) {
            [self.view makeToast:[NSString stringWithFormat:@"重新布局房间失败，请退出重进 %@",error.localizedDescription]];
            return;
        }
        [[NSUserDefaults standardUserDefaults] setObject:profile.snapshot.room.rtcCid forKey:kLastRtcCid];
        
        weakSelf.profile = profile;
        weakSelf.muteChat = profile.snapshot.room.states.muteChat.value;
        weakSelf.userIsShareScreen = false;
        if (profile.snapshot.room.states.step.value == NEEduLessonStateClassIn) {
            weakSelf.lessonStateView.hidden = YES;
            NSString *urlString = self.useFastLive ? profile.snapshot.room.properties.live.pullRtsUrl : profile.snapshot.room.properties.live.pullRtmpUrl;
            [weakSelf initLivePlayer:urlString];
            [weakSelf.player prepareToPlay];
            // add handsup item
            [weakSelf addHandsUpMenue];
        }else {
            weakSelf.lessonStateView.hidden = NO;
        }
        weakSelf.room = profile.snapshot.room;
        
        [weakSelf.maskView.navView updateRoomState:weakSelf.room serverTime:profile.ts];
        
        for (NEEduHttpUser *user in profile.snapshot.members) {
            NIMChatroomMember *chatMember = [[NIMChatroomMember alloc] init];
            chatMember.roomNickname = user.userName;
            [weakSelf.members addObject:chatMember];
        }
        [weakSelf.collectionView reloadData];
        //load chatroom function
        [weakSelf addChatroom:weakSelf.room];
    }];
    [[UIApplication sharedApplication] setIdleTimerDisabled:YES];
}
- (NSArray <NEEduHttpUser *> *)placeholderMembers {
    return @[[NEEduHttpUser teacher]];
}
- (void)initLivePlayer:(NSString *)urlString {
    if (!urlString.length) return;
    NSURL *url = [[NSURL alloc] initWithString:urlString];
    NSError *error;
    NELivePlayerController *player = [[NELivePlayerController alloc] initWithContentURL:url error:&error];
    if (error) {
        NSLog(@"error:%@",error);
        return;
    }
    [self addNotificaton];

    player.view.frame = self.contentView.bounds;
    [self.contentView addSubview:player.view];
    self.player = player;
    [self.player setScalingMode:NELPMovieScalingModeAspectFit];
}
- (void)addNotificaton {
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPrepared:) name:NELivePlayerDidPreparedToPlayNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayStateChange:) name:NELivePlayerPlaybackStateChangedNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onPlayFinished:) name:NELivePlayerPlaybackFinishedNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onSeeked:) name:NELivePlayerMoviePlayerSeekCompletedNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onLoadFirstFrame:) name:NELivePlayerFirstVideoDisplayedNotification object:nil];
}
#pragma mark - NEEduMessageServiceDelegate
- (void)onLessonStateChange:(NEEduLessonStep *)step roomUuid:(NSString *)roomUuid {
    //0/1/2 (初始化/已开始/已结束)
    [[NSUserDefaults standardUserDefaults] setObject:roomUuid forKey:kLastRoomUuid];
    
    [self.maskView.navView updateRoomState:self.room serverTime:step.time];
    if (step.value == NEEduLessonStateClassIn) {
        self.lessonStateView.hidden = YES;
        return ;
    }
    if (step.value == NEEduLessonStateClassOver) {
        [self classOver];
        return;
    }
    if (![[NEEduManager shared].localUser isTeacher]) {
        if (step.value == NEEduLessonStateClassIn) {
            self.lessonStateView.hidden = YES;
        }
    }
}
- (void)onUserInWithUser:(NEEduHttpUser *)user members:(NSArray *)members {
    NSLog(@"onUserIn user:%@ members:%@",user,members);
    NSLog(@"onUserIn members:%@",self.totalMembers);
    if ([user.role isEqualToString:NEEduRoleHost]) {
        [self.totalMembers replaceObjectAtIndex:0 withObject:user];
        [self.subscribeSet addIndex:user.rtcUid];
        [NEEduManager shared].rtcService.subscribeCacheList = self.subscribeSet;
    }else {
        if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
            //自己
            [self.totalMembers insertObject:user atIndex:1];
        }else {
            [self.totalMembers addObject:user];
        }
    }
    [self.collectionView reloadData];
}
- (NSArray <NEEduHttpUser *> *)showMembersWithJoinedMembers:(NSArray <NEEduHttpUser *> *)members {
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"properties.avHandsUp.value = %d",NEEduHandsupStateTeaAccept];
    NSArray *onlineMembers = [members filteredArrayUsingPredicate:predicate];
    NSMutableArray *showMembers = onlineMembers.mutableCopy;
    NEEduHttpUser *user = members.firstObject;
    if (![user isTeacher]) {
        [showMembers insertObject:[NEEduHttpUser teacher] atIndex:0];
    }else{
        [showMembers insertObject:user atIndex:0];
    }
    NSLog(@"onlineMembers:%@",onlineMembers);
    return showMembers;
}

- (void)onUserOutWithUser:(NEEduHttpUser *)user members:(nonnull NSArray *)members {
    NEEduHttpUser *placeholdUser = [[NEEduHttpUser alloc] init];
    if ([user.role isEqualToString:NEEduRoleHost]) {
        placeholdUser.role = NEEduRoleHost;
        [self.totalMembers replaceObjectAtIndex:0 withObject:placeholdUser];
        [self.members replaceObjectAtIndex:0 withObject:placeholdUser];
        //大班课手动管理订阅/取消订阅
        [[NEEduManager shared].rtcService subscribeVideo:NO forUserID:user.rtcUid];
        [[NEEduManager shared].rtcService subscribeAudio:NO forUserID:user.rtcUid];
    }else {
        NEEduHttpUser *remove;
        for (NEEduHttpUser *tempUser in self.totalMembers) {
            if ([tempUser.userUuid isEqualToString:user.userUuid]) {
                remove = tempUser;
            }
        }
        if (remove) {
            [self.totalMembers removeObject:remove];
        }
    }
    [self.collectionView reloadData];

}

- (void)onUserTokenExpired:(NEEduHttpUser *)user {
    __weak typeof(self)weakSelf = self;
    [self.view makeToast:@"当前账号Token校验失败" duration:2.0 position:CSToastPositionCenter title:nil image:nil style:nil completion:^(BOOL didTap) {
        [weakSelf leaveClass];
    }];
}
// 音频流 视频流 屏幕共享辅流
- (void)onVideoStreamEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    NSMutableArray *array = [NSMutableArray arrayWithArray:self.totalMembers];
    for (int i = 0; i < array.count; i++) {
        NEEduHttpUser *tmpUser = array[i];
        if ([tmpUser.userUuid isEqualToString:user.userUuid]) {
            [array replaceObjectAtIndex:i withObject:user];
        }
    }
    self.totalMembers = array;
    [self.collectionView reloadData];
    [self updateMyselfAVItemWithUser:user];
}
- (void)onAudioStreamEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    NSMutableArray *array = [NSMutableArray arrayWithArray:self.totalMembers];
    for (int i = 0; i < array.count; i++) {
        NEEduHttpUser *tmpUser = array[i];
        if ([tmpUser.userUuid isEqualToString:user.userUuid]) {
            [array replaceObjectAtIndex:i withObject:user];
        }
    }
    self.totalMembers = array;
    [self.collectionView reloadData];
    [self updateMyselfAVItemWithUser:user];
}
- (void)onSubVideoStreamEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
        return;
    }
    NSMutableArray *members = [NSMutableArray arrayWithArray:self.totalMembers];
    for (int i = 0; i < members.count; i++) {
        NEEduHttpUser *tmpUser = members[i];
        if ([tmpUser.userUuid isEqualToString:user.userUuid]) {
            [members replaceObjectAtIndex:i withObject:user];
        }
    }
    self.members = members;
    // 设置屏幕共享画布
    NERtcVideoCanvasExtention *canvas = [[NERtcVideoCanvasExtention alloc] init];
    canvas.uid = user.rtcUid;
    if (enable) {
        canvas.container = self.shareScreenView;
    }else {
        canvas.container = nil;
    }
    [[NEEduManager shared].rtcService setupSubStreamVideo:canvas];
    self.shareScreenView.hidden = !enable;
}
- (void)onVideoAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
        [self.view makeToast:[NSString stringWithFormat:@"老师%@了你的摄像头",enable ? @"打开" : @"关闭"]];
        [self turnOnVideo:enable];
    }
}

- (void)onAudioAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
        [self.view makeToast:[NSString stringWithFormat:@"老师%@了你的麦克风",enable ? @"打开" : @"关闭"]];
        [self turnOnAudio:enable];
    }
}
- (void)turnOnAudio:(BOOL)isOn {
    [[NEEduManager shared].userService localUserAudioEnable:isOn result:^(NSError * _Nonnull error) {
        if (error) {
            [self.view makeToast:error.localizedDescription];
        }
    }];
}
- (void)turnOnVideo:(BOOL)isOn {
    [[NEEduManager shared].userService localUserVideoEnable:isOn result:^(NSError * _Nonnull error) {
        if (error) {
            [self.view makeToast:error.localizedDescription];
        }
    }];
}
- (void)onWhiteboardAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    NSMutableArray *members = [NSMutableArray arrayWithArray:self.totalMembers];
    for (int i = 0; i < members.count; i++) {
        NEEduHttpUser *tmpUser = members[i];
        if ([tmpUser.userUuid isEqualToString:user.userUuid]) {
            [members replaceObjectAtIndex:i withObject:user];
        }
    }
    self.totalMembers = members;
    self.whiteboardWritable = enable;
    //如果是自己的权限被修改 设置白板
    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
        NSString *toast = enable?@"老师授予了你白板权限":@"老师取消了你白板权限";
        [self.view makeToast:toast];
        [[NMCWhiteboardManager sharedManager] callEnableDraw:self.whiteboardWritable];
        [[NMCWhiteboardManager sharedManager] hiddenTools:!self.whiteboardWritable];
    }
    [self.collectionView reloadData];
    
}
#pragma mark - ScreenShare
- (void)onScreenShareAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    NSMutableArray *members = [NSMutableArray arrayWithArray:self.totalMembers];
    for (int i = 0; i < members.count; i++) {
        NEEduHttpUser *tmpUser = members[i];
        if ([tmpUser.userUuid isEqualToString:user.userUuid]) {
            [members replaceObjectAtIndex:i withObject:user];
        }
    }
    self.totalMembers = members;
    //如果是自己的权限被修改 更新底部菜单栏
    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
        NSString *toast = enable?@"老师授予了你共享权限":@"老师取消了你共享权限";
        [self.view makeToast:toast];
        if (enable) {
            NEEduMenuItem *shareItem = [[NEEduMenuItem alloc] initWithTitle:@"共享屏幕" image:[UIImage ne_imageNamed:@"menu_share_screen"]];
            shareItem.type = NEEduMenuItemTypeShareScreen;
            shareItem.selectTitle = @"停止共享";
            [shareItem setSelctedImage:[UIImage ne_imageNamed:@"menu_share_screen_stop"]];
            [self.maskView insertItem:shareItem atIndex:2];
        }else {
            [self.maskView removeItemType:NEEduMenuItemTypeShareScreen];
//            [self stopRecord];//这个iOS12的系统没有回调
            [self stopAllScreenShare];
        }
    }
}
- (void)turnOnShareScreen:(BOOL)isOn item:(NEEduMenuItem *)item {
    if ([NEEduManager shared].profile.snapshot.room.states.step.value == NEEduLessonStateNone) {
        [self.view makeToast:@"请先开始上课"];
        return;
    }
    if (@available(iOS 12.0, *)) {
        if (isOn) {
            if (!self.shareScreenView.hidden) {
                [self.view makeToast:@"正在共享中，请稍后再试"];
                return;
            }
            if (self.isSharing) {
                [self.view makeToast:@"共享还未结束，请稍后再试"];
                return;
            }
            __weak typeof(self)weakSelf = self;
            [self.view showAlertViewOnVC:self withTitle:@"屏幕共享" subTitle:@"确认开启屏幕共享" confirm:^{
                [weakSelf startRecording];
            }];
        }else {
            [self stopAllScreenShare];
        }
    }else {
        [self.view makeToast:@"屏幕共享仅支持iOS12.0及以上版本"];
    }
}
- (void)startScreenShare {
    // RTC开启共享辅流
    NERtcVideoSubStreamEncodeConfiguration *config = [[NERtcVideoSubStreamEncodeConfiguration alloc] init];
    int result = [NERtcEngine.sharedEngine startScreenCapture:config];
    if (result != 0) {
        [self.view makeToast:[NSString stringWithFormat:@"Rtc开启屏幕共享失败code:%d",result]];
        [self.shareHost stopBroadcaster];
        return;
    }
    __weak typeof(self)weakSelf = self;
    [[NEEduManager shared].userService localShareScreenEnable:YES result:^(NSError * _Nonnull error) {
        if (error) {
            [weakSelf.view makeToast:error.localizedDescription];
            [self.shareHost stopBroadcaster];
        }else {
            weakSelf.isSharing = YES;
            weakSelf.shareScreenMask.hidden = NO;
            [weakSelf updateShareItemWithSelected:YES];
        }
    }];
}
- (void)startRecording {
    if (self.isSharing) {
        return;
    }
    [self setupShareKit];
    if (@available(iOS 12.0, *)) {
        [self.shareHost launchBroadcaster];
    }
}
- (void)setupShareKit {
    if (!self.shareHost) {
        NEScreenShareHostOptions *options = [[NEScreenShareHostOptions alloc] init];
        options.appGroup = kAppGroup;
        options.delegate = self;
        self.shareHost = [[NEScreenShareHost alloc] initWithOptions:options];
    }
}
- (void)stopScreenShare:(BOOL)isShow {
    __weak typeof(self)weakSelf = self;
    [[NEEduManager shared].userService localShareScreenEnable:NO result:^(NSError * _Nonnull error) {
        if (error) {
            if(!isShow){
                return;
            }
            dispatch_async(dispatch_get_main_queue(), ^{
                [weakSelf.view makeToast:error.localizedDescription];
            });
        }else {
            weakSelf.isSharing = NO;
            weakSelf.shareScreenMask.hidden = YES;
            [weakSelf updateShareItemWithSelected:NO];
            [weakSelf.shareHost stopBroadcaster];
            [NERtcEngine.sharedEngine stopScreenCapture];
        }
    }];
}
- (void)stopAllScreenShare {
    if (!self.isSharing) {
        return;
    }
    [self stopScreenShare:YES];
}
///更新屏幕共享按钮
- (void)updateShareItemWithSelected:(BOOL)selected {
    for (NEEduMenuItem *item in [self.maskView.stackView arrangedSubviews]) {
        if (item.type == NEEduMenuItemTypeShareScreen) {
            item.isSelected = selected;
            break;
        }
    }
}
/// 仅停止录制和rtc辅流，不发送请求。
- (void)stopRecord {
//    self.isSharing = NO;
    self.shareScreenMask.hidden = YES;
    [self.shareHost stopBroadcaster];
    [NERtcEngine.sharedEngine stopScreenCapture];
}


- (void)removeNotification {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}
- (void)updateMyselfAVItemWithUser:(NEEduHttpUser *)user {
    if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
        for (int i = 0; i < self.maskView.stackView.arrangedSubviews.count; i ++) {
            NEEduMenuItem *item = self.maskView.stackView.arrangedSubviews[i];
            if (item.type == NEEduMenuItemTypeVideo) {
                item.isSelected = !user.streams.video.value;
            }
            if (item.type == NEEduMenuItemTypeAudio) {
                item.isSelected = !user.streams.audio.value;
            }
            if (item.type == NEEduMenuItemTypeShareScreen) {
                item.isSelected = user.streams.subVideo.value;
            }
        }
    }
}
#pragma mark - NEScreenShareHostDelegate
- (void)onBroadcastStarted {
    [self startScreenShare];
}

- (void)onBroadcastFinished {
    if (self.isSharing) {
        int result = [NERtcEngine.sharedEngine stopScreenCapture];
        if (result != 0) {
            [self.view makeToast:[NSString stringWithFormat:@"Rtc停止共享失败:%d",result]];
            return;
        }
        __weak typeof(self)weakSelf = self;
        [[NEEduManager shared].userService localShareScreenEnable:NO result:^(NSError * _Nonnull error) {
            if (error) {
                [self.view makeToast:error.localizedDescription];
            }else {
                weakSelf.isSharing = NO;
                weakSelf.shareScreenMask.hidden = YES;
                [weakSelf updateShareItemWithSelected:NO];
            }
        }];
    }
}

- (void)onBroadcastPaused {
}

- (void)onBroadcastResumed {
}

- (void)onReceiveVideoFrameInfo:(NSDictionary<NSString *,id> *)videoFrameInfo {
    NERtcVideoFrame *frame = [[NERtcVideoFrame alloc] init];
    frame.format = kNERtcVideoFormatI420;
    frame.width = [videoFrameInfo[NEScreenShareVideoFrameWidthKey] unsignedIntValue];
    frame.height = [videoFrameInfo[NEScreenShareVideoFrameHeightKey] unsignedIntValue];
    frame.buffer = (void *)[videoFrameInfo[NEScreenShareVideoFrameDataKey] bytes];
    frame.timestamp = [videoFrameInfo[NEScreenShareVideoFrameTimestampKey] unsignedLongLongValue];
    NSNumber *orientation = videoFrameInfo[NEScreenShareVideoFrameOrientationKey];
    switch (orientation.unsignedIntValue) {
        case kCGImagePropertyOrientationLeft: {
            frame.rotation = kNERtcVideoRotation_90;
            break;
        }
        case kCGImagePropertyOrientationRight: {
            frame.rotation = kNERtcVideoRotation_270;
            break;
        }
        default:
            break;
    }
    int ret = [NERtcEngine.sharedEngine pushExternalVideoFrame:frame];
    if (ret != 0 && ret != kNERtcErrFatal) {
        NSLog(@"发送视频流失败: %@", NERtcErrorDescription(ret));
        return;
    }
}
#pragma mark - notification
- (void)onPrepared:(NSNotification *)notification {
    [self.player play];
    NSLog(@"player:%s",__func__);
}
- (void)onPlayStateChange:(NSNotification *)notification {
    NSLog(@"player:%s",__func__);
    
}
- (void)onPlayFinished:(NSNotification *)notification {
    NSLog(@"player:%s",__func__);
    if ([notification.userInfo[@"NELivePlayerPlaybackDidFinishErrorUserInfoKey"] longValue] == -1002){
        NSTimer *timer = [NSTimer timerWithTimeInterval:1.0 target:self selector:@selector(update) userInfo:nil repeats:NO];
        [[NSRunLoop currentRunLoop] addTimer:timer forMode:NSDefaultRunLoopMode];
    }
}
- (void)update {
    NSString *urlString = self.useFastLive ? self.profile.snapshot.room.properties.live.pullRtsUrl : self.profile.snapshot.room.properties.live.pullRtmpUrl;
    [self initLivePlayer:urlString];
    [self.player prepareToPlay];
}
- (void)onSeeked:(NSNotification *)notification {
    NSLog(@"player:%s",__func__);
}
- (void)onLoadFirstFrame:(NSNotification *)notification {
    NSLog(@"player:%s",__func__);
}


#pragma mark - NEEduIMChatDelegate
- (void)didRecieveChatMessages:(NSArray<NIMMessage *> *)messages {
    //接收成功
    NIMMessage *imMessage = messages.firstObject;
    NSLog(@"imMessage: %ld %@", imMessage.messageType, imMessage.messageObject);
    NIMMessageChatroomExtension *ext = imMessage.messageExt;
    NEEduChatMessage *message = [[NEEduChatMessage alloc] init];
    message.imMessage = imMessage;
    message.userName = ext.roomNickname;
    message.content = imMessage.text;
    message.myself = NO;
    message.timestamp = [[NSDate date] timeIntervalSince1970];
    if([imMessage messageType] == NIMMessageTypeImage) {
        message.type = NEEduChatMessageTypeImage;
        NIMImageObject *originMessageObject = [imMessage messageObject];
        message.imageUrl = originMessageObject.url;
        message.imageThumbUrl = originMessageObject.thumbUrl;
        [[SDWebImageDownloader sharedDownloader] downloadImageWithURL:[NSURL URLWithString:originMessageObject.thumbUrl] completed:^(UIImage * _Nullable image, NSData * _Nullable data, NSError * _Nullable error, BOOL finished) {
            if (error) {
                return;
            }
            message.thumbImage = image;
            message.contentSize = [image ne_showSizeWithMaxWidth:176 maxHeight:190];
            [self addTimeMessage:message];
            [self addMessage:message];
            if (self.chatVC.presentingViewController) {
                [self.chatVC reloadTableViewToBottom:YES];
            }else {
                self.chatItem.badgeLabel.hidden = NO;
            }
        }];
        
    }else if ([imMessage messageType] == NIMMessageTypeText) {
        message.type = NEEduChatMessageTypeText;
        message.contentSize = [imMessage.text sizeWithWidth:(self.view.bounds.size.width - 112) font:[UIFont systemFontOfSize:14]];
        [self addTimeMessage:message];
        [self addMessage:message];
        if (self.chatVC.presentingViewController) {
            [self.chatVC reloadTableViewToBottom:YES];
        }else {
            self.chatItem.badgeLabel.hidden = NO;
        }
    } else if ([imMessage messageType] == NIMMessageTypeCustom) {
        NIMCustomObject *custom = imMessage.messageObject;
        NEEduIMAttach *attach = custom.attachment;
        NEEduSignalMessage *message = attach.data;
        NSDictionary *states = message.data[@"states"];
        NSDictionary *step = [states objectForKey:@"step"];
        NSString *reason = message.data[@"reason"];
        if (step) {
            [[NSUserDefaults standardUserDefaults] setObject:self.room.roomUuid forKey:kLastRoomUuid];
            NSNumber *value = step[@"value"];
            NSNumber *time = step[@"time"];
            if ([value isEqualToNumber:@(1)]) {
                //start Class
                [self startClassWithServerTime:time.integerValue];
            }else if([value isEqualToNumber:@(2)]) {
                // end Class
                [self classOver];
            }
            return;
        }
        if ([reason isEqualToString:@"ALL_MEMBERS_OUT"]){
            [self classOver];
        }
        NSDictionary *muteChat = [states objectForKey:@"muteChat"];
        if (muteChat) {
            NSNumber *value = muteChat[@"value"];
            if ([value isEqualToNumber:@(1)]) {
                self.muteChat = YES;
                //muteChat
                if (self.chatVC) {
                    [self.chatVC updateMuteChat:YES];
                }
            }else {
                self.muteChat = NO;
                [self.chatVC updateMuteChat:NO];
            }
            return;
        }
        
    }else if([imMessage messageType] == NIMMessageTypeNotification){
        //user in/out
        NSLog(@"class:%@",[imMessage.messageObject class]);
        NIMNotificationObject *object = imMessage.messageObject;
        NSLog(@"content:%@",object.content);
        NIMNotificationContent *content = object.content;
        NSInteger eventType = [[content valueForKey:@"eventType"] integerValue];
        NIMChatroomNotificationMember *member = [content valueForKey:@"source"];
        NSLog(@"nickName:%@",member.nick);
        if (eventType == 301) {
            //user in
            if (self.membersVC) {
                NIMChatroomMember *chatMember = [[NIMChatroomMember alloc] init];
                chatMember.roomNickname = member.nick;
                chatMember.userId = member.userId;
                [self.membersVC addMember:chatMember];
            }
            [self.collectionView reloadData];
        }else if (eventType == 302) {
            //user out
            if (self.membersVC) {
                NIMChatroomMember *chatMember = [[NIMChatroomMember alloc] init];
                chatMember.roomNickname = member.nick;
                chatMember.userId = member.userId;
                [self.membersVC removeMember:chatMember];
            }
            [self.collectionView reloadData];
        }
    }
}

- (void)willSendMessage:(NIMMessage *)message {
    //去重
    NSLog(@"willSendMessage:%@ state:%ld",message.messageId,(long)(message.deliveryState));
    for (NEEduChatMessage *eduMessage in self.messages) {
        if ([eduMessage.imMessage.messageId isEqualToString:message.messageId]) {
            eduMessage.sendState = NEEduChatMessageSendStateNone;
            if (self.chatVC.presentingViewController) {
                [self.chatVC reloadTableViewToBottom:NO];
            }
            return;
        }
    }
    NEEduChatMessage *eduMessage = [[NEEduChatMessage alloc] init];
    eduMessage.imMessage = message;
    NSString *role = [[NEEduManager shared].localUser isTeacher] ? @"老师":@"学生";
    eduMessage.userName = [NSString stringWithFormat:@"%@(%@)",self.userName,role];
    eduMessage.content = message.text;
    if([message messageType] == NIMMessageTypeImage) {
        NIMImageObject *originMessageObject = [message messageObject];
        UIImage *thumbImage = [UIImage imageWithContentsOfFile:originMessageObject.thumbPath];
        eduMessage.contentSize = [thumbImage ne_showSizeWithMaxWidth:176 maxHeight:190];
        eduMessage.thumbImage = thumbImage;
        eduMessage.imageUrl = originMessageObject.path;
        eduMessage.imageThumbUrl = originMessageObject.thumbPath;
        eduMessage.type = NEEduChatMessageTypeImage;
    }else {
        eduMessage.type = NEEduChatMessageTypeText;
        eduMessage.contentSize = [message.text sizeWithWidth:(self.view.bounds.size.width - 112) font:[UIFont systemFontOfSize:14]];
    }
    eduMessage.myself = YES;
    eduMessage.timestamp = [[NSDate date] timeIntervalSince1970];
    eduMessage.sendState = NEEduChatMessageSendStateNone;
    
    [self addTimeMessage:eduMessage];
    [self addMessage:eduMessage];
    NSLog(@"sendMessage count:%lu",(unsigned long)(self.messages.count));
    if (self.chatVC) {
        [self.chatVC reloadTableViewToBottom:YES];
    }
}

- (void)didSendMessage:(NIMMessage *)message error:(NSError *)error {
    NSLog(@"didSendMessage:%@ error:%@",message.messageId,error);
    for (NEEduChatMessage *eduMessage in self.messages) {
        if ([eduMessage.imMessage.messageId isEqualToString:message.messageId]) {
            eduMessage.sendState = error ? NEEduChatMessageSendStateFailure :NEEduChatMessageSendStateSuccess;;
            if (self.chatVC.presentingViewController) {
                [self.chatVC reloadTableViewToBottom:NO];
            }
            return;
        }
    }
}
- (void)addTimeMessage:(NEEduChatMessage *)message {
    //添加时间显示消息
    NEEduChatMessage *lastMessage = self.messages.lastObject;
    if (lastMessage.type == NEEduChatMessageTypeText) {
        CGFloat dur = message.timestamp - lastMessage.timestamp;
        CGFloat min = dur/60;
        if (min > 5) {
            NEEduChatMessage *timeMessage = [[NEEduChatMessage alloc] init];
            timeMessage.content = [NSString stringFromDate:[NSDate date]];
            timeMessage.myself = NO;
            timeMessage.contentSize = [timeMessage.content sizeWithWidth:(self.view.bounds.size.width - 112) font:[UIFont systemFontOfSize:14]];
            timeMessage.type = NEEduChatMessageTypeTime;
            [self addMessage:timeMessage];
        }
    }
}

- (void)addMessage:(NEEduChatMessage *)message {
    if (self.messages.count >= 5000) {
        [self.messages removeObjectAtIndex:0];
    }
    [self.messages addObject:message];
}
//进入房间后开始上课走的这里
- (void)startClassWithServerTime:(NSInteger)time {
    if (self.room.states.step.value == NEEduLessonStateClassIn) {
        return;
    }
    self.lessonStateView.hidden = YES;
    NEEduLessonStep *step = [[NEEduLessonStep alloc] init];
    step.value = NEEduLessonStateClassIn;
    step.time = time;
    self.room.states.step = step;
    [self.maskView.navView updateRoomState:self.room serverTime:time];
    //player
    NSString *urlString = self.useFastLive ? self.profile.snapshot.room.properties.live.pullRtsUrl : self.profile.snapshot.room.properties.live.pullRtmpUrl;
    [self initLivePlayer:urlString];
    [self.player prepareToPlay];
//    [self addHandsUpMenue];
    [self.maskView insertItem:self.handsupItem atIndex:1];
    [self.collectionView reloadData];
}

#pragma mark - NEEduRoomServiceDelegate
- (void)netStateChangeWithState:(AFNetworkReachabilityStatus)state {
    switch (state) {
        case AFNetworkReachabilityStatusUnknown:
        case AFNetworkReachabilityStatusNotReachable:
            self.maskView.navView.netStateView.image = [UIImage ne_imageNamed:@"net_0"];
            break;
        case AFNetworkReachabilityStatusReachableViaWWAN:
        case AFNetworkReachabilityStatusReachableViaWiFi:{
            self.maskView.navView.netStateView.image = [UIImage ne_imageNamed:@"net_3"];
            __weak typeof(self)weakSelf = self;
            NCKLogInfo(@"LiveRoom net change:%d",state);
            [[NEEduManager shared].roomService getRoomProfile:weakSelf.room.roomUuid completion:^(NSError * _Nonnull error, NEEduRoomProfile * _Nonnull profile) {
                if(error){
                    if(error.code == NEEduErrorTypeRoomNotFound){
                        [self classOver];
                    }
                    return;
                }
                weakSelf.room = profile.snapshot.room;
                /**
                 1.房间状态刷新
                 2.更新学生列表
                 3.更新用户权限的变化（音视频开关/白板/屏幕共享）
                 4.更新屏幕共享
                 5.更新举手 上台
                 6.更新成员列表
                 */
                [weakSelf updateUIWithRoom:profile.snapshot.room];
                [weakSelf membersWithProfile:profile];
                
                [weakSelf updateMemberAuthorizationWithProfile:profile];
                [weakSelf updateScreenShare];
                
                [weakSelf updateHandsupStateWithMembers:profile.snapshot.members];
//                [weakSelf updateMemberVCWithProfile:profile];
                [weakSelf.membersVC setRefresh];
            }];
            break;
        }
        default:
            break;
    }
}

- (void)updateMemberAuthorizationWithProfile:(NEEduRoomProfile *)profile {
    NSLog(@"video:%@ audio:%@",[NEEduManager shared].localUser.properties.streamAV.video,[NEEduManager shared].localUser.properties.streamAV.audio);
    for (NEEduHttpUser *user in profile.snapshot.members) {
        if ([user.userUuid isEqualToString:[NEEduManager shared].localUser.userUuid]) {
            NEEduHttpUser *localUser = [NEEduManager shared].localUser;
            if (user.properties.streamAV.video) {
                //update video
                if (localUser.properties.streamAV.video && ![user.properties.streamAV.video isEqualToNumber:localUser.properties.streamAV.video]) {
                    [self onVideoAuthorizationEnable:user.properties.streamAV.video.boolValue user:user];
                }else {
                    localUser.properties.streamAV.video = user.properties.streamAV.video;
                    [self onVideoAuthorizationEnable:user.properties.streamAV.video.boolValue user:user];
                }
            }
            if (user.properties.streamAV.audio) {
                //update audio
                if (localUser.properties.streamAV.audio && ![user.properties.streamAV.audio isEqualToNumber:localUser.properties.streamAV.audio]) {
                    [self onAudioAuthorizationEnable:user.properties.streamAV.audio.boolValue user:user];
                }else {
                    localUser.properties.streamAV.audio = user.properties.streamAV.audio;
                    [self onAudioAuthorizationEnable:user.properties.streamAV.audio.boolValue user:user];
                }
            }
            
            if (user.properties.whiteboard.drawable != localUser.properties.whiteboard.drawable) {
                [self onWhiteboardAuthorizationEnable:user.properties.whiteboard.drawable user:user];
            }
            if (user.properties.screenShare.value != localUser.properties.screenShare.value) {
                [self onScreenShareAuthorizationEnable:user.properties.screenShare.value user:user];
            }
            break;
        }
    }
}

#pragma mark - NMCWhiteboardManagerDelegate
- (void)onWebPageLoaded {
    NSLog(@"wb: onWebPageLoaded");
    NMCWebLoginParam *param = [[NMCWebLoginParam alloc] init];
    param.appKey = [NEEduManager shared].imKey;
    param.uid = @([NEEduManager shared].localUser.rtcUid);
    param.channelName = [NEEduManager shared].profile.snapshot.room.properties.whiteboard.channelName;
    param.record = YES;
    param.debug = NO;
    param.nickname = [NEEduManager shared].localUser.userName;
    [[NMCWhiteboardManager sharedManager] callWebJoinRoom:param];
}

- (void)onWebGetAuth {
    NSLog(@"wb: onWebGetAuth");
    [[NMCWhiteboardManager sharedManager] sendAuthNonce:[NEEduManager shared].localUser.wbAuth.nonce curTime:[NEEduManager shared].localUser.wbAuth.curTime checksum:[NEEduManager shared].localUser.wbAuth.checksum];
}

- (void)onWebLoginIMSucceed {
    NSLog(@"wb: onWebLoginIMSucceed");
}

- (void)onWebCreateWBSucceed {
}

- (void)onWebJoinWBSucceed {
    NMCTool *tool = [[NMCTool alloc] init];
    WhiteboardItem *selectionItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameSelection];
    WhiteboardItem *penItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNamePen];
    WhiteboardItem *shapeItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameShape];
    WhiteboardItem *zoomLevel = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameZoomLevel];
    WhiteboardItem *moreItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameMore];
    WhiteboardItem *eraserItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameEraser];
    WhiteboardItem *clearItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameClear];
    WhiteboardItem *undoItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameUndo];
    WhiteboardItem *redoItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameRedo];
    WhiteboardItem *zoomInItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameZoomIn];
    WhiteboardItem *zoomOutItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameZoomOut];
    WhiteboardItem *fitToContent = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameFitToConent];
    WhiteboardItem *fitToDoc = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameFitToDoc];
    WhiteboardItem *pan = [[WhiteboardItem alloc] initWithName:WhiteboardItemNamePan];
    WhiteboardItem *uploadDoc = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameUpload];
    WhiteboardItem *visionLock = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameVisionLock];
    
    moreItem.subItems = @[eraserItem,clearItem,undoItem,redoItem, uploadDoc];
    tool.items = @[selectionItem,penItem,shapeItem,moreItem];
    tool.position = WhiteboardPositionBottomRight;
    
    WhiteboardItem *topRightMoreItem = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameMore];
    topRightMoreItem.subItems = @[zoomInItem,zoomOutItem, fitToContent, fitToDoc, pan, visionLock];
    NMCTool *toolTopRight = [[NMCTool alloc] init];
    toolTopRight.items = @[topRightMoreItem,zoomLevel];
    toolTopRight.position = WhiteboardPositionTopRight;
    
    WhiteboardItem *pageInfo = [[WhiteboardItem alloc] initWithName:WhiteboardItemNamePageBoardInfo];
    WhiteboardItem *preview = [[WhiteboardItem alloc] initWithName:WhiteboardItemNamePreview];
    NMCTool *topLeftTool = [[NMCTool alloc] init];
    topLeftTool.items = @[pageInfo,preview];
    topLeftTool.position = WhiteboardPositionTopLeft;
    
    [[NMCWhiteboardManager sharedManager] setupWhiteboardTools:@[toolTopRight,tool,topLeftTool]];
    [[NMCWhiteboardManager sharedManager] callEnableDraw:self.whiteboardWritable];
    [[NMCWhiteboardManager sharedManager] setAppConfigWithPresetId:@(104868090)];
    [[NMCWhiteboardManager sharedManager] hiddenTools:!self.whiteboardWritable];
}


- (void)onWebJoinWBFailed:(NSInteger)code error:(NSString *)error {
    NSLog(@"wb:onWebJoinWBFailed : %ld, %@",(long)code, error);
    [self showToastView:error];
}

- (void)onWebCreateWBFailed:(NSInteger)code error:(NSString *)error {
    NSLog(@"wb:onWebCreateWBFailed : %ld, %@",(long)code, error);
    [self showToastView:error];
}

- (void)onWebLeaveWB {
    NSLog(@"wb:onWebLeaveWB");
}

- (void)onWebError:(NSInteger)code error:(NSString *)error {
    NSLog(@"wb:onWebError : %ld, %@",(long)code, error);
    [self showToastView:error];
}

- (void)onWebJsError:(NSString *)error {
    NSLog(@"wb:onWebError : %@", error);
}

- (void)showToastView:(NSString *)error {
    if (error && error.length > 0) {
        [self.view makeToast:error duration:2.0 position:CSToastPositionCenter];
        [self leaveRoom];
    }
}
- (void)leaveRoom {
    [[NMCWhiteboardManager sharedManager] callWebLogoutIM];
    [[NMCWhiteboardManager sharedManager] clearWebViewCache];
}
@end

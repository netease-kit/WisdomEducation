//
//  NEEduClassRoomVC.m
//  EduUI
//
//  Created by Groot on 2021/5/19.
//  Copyright © 2021 NetEase. All rights reserved.
//  Use of this source code is governed by a MIT license that can be found in the LICENSE file
//

#import "NEEduClassRoomVC.h"
#import "UIImage+NE.h"
#import "NEEduChatViewController.h"
#import "UIView+Toast.h"

#import <NEWhiteBoard/WhiteBoardStateModel.h>
#import <NEWhiteBoard/NMCTool.h>
#import "NEEduLessonStateView.h"
#import "NEEduLessonOverView.h"
#import "NEEduLessonInfoView.h"
#import "UIView+NE.h"
#import "NSString+NE.h"

@interface NEEduClassRoomVC ()<NEEduRoomViewMaskViewDelegate,NMCWhiteboardManagerDelegate,NEScreenShareHostDelegate,NEEduVideoServiceDelegate,NEEduIMChatDelegate,NEEduMessageServiceDelegate,NEEduRoomServiceDelegate>

/// 自己是否在共享
@property (nonatomic, assign) BOOL isSharing;
@property (nonatomic, strong) NEEduLessonStateView *lessonStateView;
@property (nonatomic, strong) NEEduLessonOverView *classOverView;
@property (nonatomic, strong) NEEduChatViewController *chatVC;
@property (nonatomic, strong) NEEduLessonInfoView *infoView;
//用于chatVC新创建时同步上一次数据
@property (nonatomic, strong) NSMutableArray<NEEduChatMessage *> *messages;
@property (nonatomic, assign) BOOL netReachable;

@end
static NSString *kAppGroup = @"group.com.netease.yunxin.app.wisdom.education";

@implementation NEEduClassRoomVC

- (void)viewDidLoad {
    [super viewDidLoad];
    self.netReachable = YES;
    [self.navigationController setNavigationBarHidden:NO];
    self.view.backgroundColor = [UIColor whiteColor];
    [EduManager shared].messageService.delegate = self;
    [EduManager shared].videoService.delegate = self;
    [EduManager shared].roomService.delegate = self;
    [EduManager shared].imService.chatDelegate = self;
    self.whiteboardWritable = YES;
    [self initMenuItems];
    [self setupSubviews];
    [self.maskView.navView.infoButton addTarget:self action:@selector(infoButtonClick:) forControlEvents:UIControlEventTouchUpInside];
    [[UIApplication sharedApplication] setIdleTimerDisabled:YES];

    [self updateUIWithRoom:self.room];
    [self updateScreenShare];
    
}
- (void)viewWillAppear:(BOOL)animated {
    [[UIDevice currentDevice] setValue:[NSNumber numberWithInteger:UIInterfaceOrientationLandscapeRight] forKey:@"orientation"];
}

- (void)setupSubviews {
    self.view.backgroundColor = [UIColor colorWithRed:26/255.0 green:32/255.0 blue:40/255.0 alpha:1.0];
    [self.view addSubview:self.maskView];
    NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:self.maskView attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *top;
    if (@available(iOS 11.0, *)) {
        top = [NSLayoutConstraint constraintWithItem:self.maskView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view.safeAreaLayoutGuide attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    } else {
        top = [NSLayoutConstraint constraintWithItem:self.maskView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    }
    NSLayoutConstraint *right = [NSLayoutConstraint constraintWithItem:self.maskView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    NSLayoutConstraint *bottom = [NSLayoutConstraint constraintWithItem:self.maskView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.view.safeAreaLayoutGuide attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self.view addConstraints:@[left,top,right,bottom]];
    
    [self.view addSubview:self.boardView];
    NSLayoutConstraint *editeLeft = [NSLayoutConstraint constraintWithItem:self.boardView attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeading multiplier:1.0 constant:0];
    NSLayoutConstraint *editeTop;
    NSLayoutConstraint *editeBottom;
    if (@available(iOS 11.0, *)) {
        editeTop = [NSLayoutConstraint constraintWithItem:self.boardView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view.safeAreaLayoutGuide attribute:NSLayoutAttributeTop multiplier:1.0 constant:40];
        editeBottom = [NSLayoutConstraint constraintWithItem:self.boardView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.view.safeAreaLayoutGuide attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-60];
    }else {
        editeTop = [NSLayoutConstraint constraintWithItem:self.boardView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTop multiplier:1.0 constant:40];
        editeBottom = [NSLayoutConstraint constraintWithItem:self.boardView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-60];
    }
    [self.view addConstraints:@[editeLeft,editeTop,editeBottom]];
    
    [self.view addSubview:self.shareScreenView];
    NSLayoutConstraint *shareViewLeft = [NSLayoutConstraint constraintWithItem:self.shareScreenView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.boardView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
    NSLayoutConstraint *shareViewRight = [NSLayoutConstraint constraintWithItem:self.shareScreenView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.boardView attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    NSLayoutConstraint *shareViewTop = [NSLayoutConstraint constraintWithItem:self.shareScreenView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.boardView attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *shareViewBottom = [NSLayoutConstraint constraintWithItem:self.shareScreenView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.boardView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self.view addConstraints:@[shareViewLeft,shareViewRight,shareViewTop,shareViewBottom]];
    
    [self.view addSubview:self.lessonStateView];
    NSLayoutConstraint *stateViewLeft = [NSLayoutConstraint constraintWithItem:self.lessonStateView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.boardView attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
    NSLayoutConstraint *stateViewRight = [NSLayoutConstraint constraintWithItem:self.lessonStateView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.boardView attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    NSLayoutConstraint *stateViewTop = [NSLayoutConstraint constraintWithItem:self.lessonStateView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.boardView attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *stateViewBottom = [NSLayoutConstraint constraintWithItem:self.lessonStateView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.boardView attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self.view addConstraints:@[stateViewLeft,stateViewRight,stateViewTop,stateViewBottom]];
    
    
    [self.view addSubview:self.collectionView];
    NSLayoutConstraint *collectionViewRight = [NSLayoutConstraint constraintWithItem:self.collectionView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeRight multiplier:1.0 constant:- 20];
    
    NSLayoutConstraint *collectionViewTop;
    NSLayoutConstraint *collectionViewBottom;
    if (@available(iOS 11.0, *)) {
        collectionViewTop = [NSLayoutConstraint constraintWithItem:self.collectionView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view.safeAreaLayoutGuide attribute:NSLayoutAttributeTop multiplier:1.0 constant:50];
        collectionViewBottom = [NSLayoutConstraint constraintWithItem:self.collectionView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.view.safeAreaLayoutGuide attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-60];
    }else {
        collectionViewTop = [NSLayoutConstraint constraintWithItem:self.collectionView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTop multiplier:1.0 constant:50];
        collectionViewBottom = [NSLayoutConstraint constraintWithItem:self.collectionView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeBottom multiplier:1.0 constant:-60];
    }
    NSLayoutConstraint *collectionViewWidth = [NSLayoutConstraint constraintWithItem:self.collectionView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:120];
    NSLayoutConstraint *collectionViewLeft = [NSLayoutConstraint constraintWithItem:self.collectionView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.boardView attribute:NSLayoutAttributeRight multiplier:1.0 constant:8];

    [self.view addConstraints:@[collectionViewRight,collectionViewTop,collectionViewBottom,collectionViewLeft]];
    [self.collectionView addConstraint:collectionViewWidth];
    
    [self.view addSubview:self.classOverView];
    NSLayoutConstraint *classOverLeft = [NSLayoutConstraint constraintWithItem:self.classOverView attribute:NSLayoutAttributeLeft relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeLeft multiplier:1.0 constant:0];
    NSLayoutConstraint *classOverRight = [NSLayoutConstraint constraintWithItem:self.classOverView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeRight multiplier:1.0 constant:0];
    NSLayoutConstraint *classOverTop = [NSLayoutConstraint constraintWithItem:self.classOverView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
    NSLayoutConstraint *classOverBottom = [NSLayoutConstraint constraintWithItem:self.classOverView attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeBottom multiplier:1.0 constant:0];
    [self.view addConstraints:@[classOverLeft,classOverRight,classOverTop,classOverBottom]];
}
- (void)initMenuItems {
    NEEduMenuItem *audoItem = [[NEEduMenuItem alloc] initWithTitle:@"静音" image:[UIImage ne_imageNamed:@"menu_audio"]];
    audoItem.selectTitle = @"取消静音";
    audoItem.type = NEEduMenuItemTypeAudio;
    [audoItem setSelctedImage:[UIImage ne_imageNamed:@"menu_audio_off"]];
    
    NEEduMenuItem *videoItem = [[NEEduMenuItem alloc] initWithTitle:@"关闭摄像头" image:[UIImage ne_imageNamed:@"menu_video"]];
    videoItem.selectTitle = @"打开摄像头";
    videoItem.type = NEEduMenuItemTypeVideo;
    [videoItem setSelctedImage:[UIImage ne_imageNamed:@"menu_video_off"]];

    NEEduMenuItem *shareItem = [[NEEduMenuItem alloc] initWithTitle:@"共享屏幕" image:[UIImage ne_imageNamed:@"menu_share_screen"]];
    shareItem.type = NEEduMenuItemTypeShareScreen;
    [shareItem setSelctedImage:[UIImage ne_imageNamed:@"menu_share_screen_stop"]];
    
    NEEduMenuItem *membersItem = [[NEEduMenuItem alloc] initWithTitle:@"课堂成员" image:[UIImage ne_imageNamed:@"menu_members"]];
    membersItem.type = NEEduMenuItemTypeMembers;
    NEEduMenuItem *chatItem = [[NEEduMenuItem alloc] initWithTitle:@"聊天室" image:[UIImage ne_imageNamed:@"menu_chat"]];
    chatItem.type = NEEduMenuItemTypeChat;
    self.menuItems = @[audoItem,videoItem,shareItem,membersItem,chatItem];
}

- (void)showViewWithItem:(NEEduMenuItem *)item {
    switch (item.type) {
        case NEEduMenuItemTypeAudio:
            [self turnOnAudio:!item.isSelected];
            break;
        case NEEduMenuItemTypeVideo:
            [self turnOnVideo:!item.isSelected];
            break;
        case NEEduMenuItemTypeShareScreen:
            [self turnOnShareScreen:!item.isSelected item:item];
            break;
        case NEEduMenuItemTypeMembers:
            [self showMembersViewWithitem:item];
            break;
        case NEEduMenuItemTypeChat:
            [self showChatViewWithitem:item];
            break;
        case NEEduMenuItemTypeHandsup:
            [self handsupItem:item];
            break;
        default:
            break;
    }
}

- (void)turnOnAudio:(BOOL)isOn {
    [[EduManager shared].userService localUserAudioEnable:isOn result:^(NSError * _Nonnull error) {
        if (error) {
            [self.view makeToast:error.localizedDescription];
        }
    }];
}
- (void)turnOnVideo:(BOOL)isOn {
    [[EduManager shared].userService localUserVideoEnable:isOn result:^(NSError * _Nonnull error) {
        if (error) {
            [self.view makeToast:error.localizedDescription];
        }
    }];
}
- (void)turnOnShareScreen:(BOOL)isOn item:(NEEduMenuItem *)item {
    if ([EduManager shared].profile.snapshot.room.states.step.value == NEEduLessonStateNone) {
        [self.view makeToast:@"还未开始上课"];
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
- (void)startRecording {
    if (self.isSharing) {
        return;
    }
    [self setupShareKit];
    [self.shareHost launchBroadcaster];
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
    [[EduManager shared].userService localShareScreenEnable:YES result:^(NSError * _Nonnull error) {
        if (error) {
            [weakSelf.view makeToast:error.localizedDescription];
            [self.shareHost stopBroadcaster];
        }else {
            weakSelf.isSharing = YES;
            [weakSelf updateShareItemWithSelected:YES];
        }
    }];
}

//发送停止共享请求、停止录制、停止Rtc辅流
- (void)stopAllScreenShare {
    if (!self.isSharing) {
        return;
    }
    __weak typeof(self)weakSelf = self;
    [[EduManager shared].userService localShareScreenEnable:NO result:^(NSError * _Nonnull error) {
        if (error) {
            [self.view makeToast:error.localizedDescription];
        }else {
            weakSelf.isSharing = NO;
            [weakSelf updateShareItemWithSelected:NO];
            [weakSelf.shareHost stopBroadcaster];
            [NERtcEngine.sharedEngine stopScreenCapture];
        }
    }];
}
/// 仅停止录制和rtc辅流，不发送请求。
- (void)stopRecord {
    self.isSharing = NO;
    [self.shareHost stopBroadcaster];
    [NERtcEngine.sharedEngine stopScreenCapture];
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
        [[EduManager shared].userService localShareScreenEnable:NO result:^(NSError * _Nonnull error) {
            if (error) {
                [self.view makeToast:error.localizedDescription];
            }else {
                weakSelf.isSharing = NO;
                [weakSelf updateShareItemWithSelected:NO];
            }
        }];
    }
}

- (void)updateShareItemWithSelected:(BOOL)selected {
    for (NEEduMenuItem *item in [self.maskView.stackView arrangedSubviews]) {
        if (item.type == NEEduMenuItemTypeShareScreen) {
            item.isSelected = selected;
            break;
        }
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

- (void)showMembersViewWithitem:(NEEduMenuItem *)item {
    if ([EduManager shared].profile.snapshot.room.states.step.value == NEEduLessonStateNone) {
        [self.view makeToast:@"还未开始上课"];
        return;
    }
    if (!self.membersVC) {
        NSMutableArray *memberArray = [NSMutableArray array];
        self.membersVC = [[NEEduMembersVC alloc] init];
        self.membersVC.muteChat = [EduManager shared].profile.snapshot.room.states.muteChat.value;
        for (NEEduHttpUser *user in [EduManager shared].profile.snapshot.members) {
            if (![user.role isEqualToString:NEEduRoleHost]) {
                NEEduMember *member = [self memberFromHttpUser:user];
                [memberArray addObject:member];
            }
        }
        self.membersVC.members = memberArray;
        self.membersVC.modalPresentationStyle = UIModalPresentationFullScreen;
    }
    [self presentViewController:self.membersVC animated:YES completion:nil];
}
- (NEEduMember *)memberFromHttpUser:(NEEduHttpUser *)user {
    NEEduMember *member = [[NEEduMember alloc] init];
    member.name = user.userName;
    member.userID = user.userUuid;
    member.hasVideo = user.streams.video.value;
    member.hasAudio = user.streams.audio.value;
    member.shareScreenEnable = user.properties.screenShare.value;
    member.whiteboardEnable = user.properties.whiteboard.drawable;
    member.online = user.properties.avHandsUp.value == NEEduHandsupStateTeaAccept ? YES : NO;
    if ([EduManager shared].localUser.roleType == NEEduRoleTypeTeacher) {
        member.videoEnable = YES;
        member.audioEnable = YES;
        member.showMoreButton = YES;
    }else {
        if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
            member.videoEnable = YES;
            member.audioEnable = YES;
        }else {
            member.videoEnable = NO;
            member.audioEnable = NO;
        }
        member.showMoreButton = NO;
    }
    if ([EduManager shared].roomService.room.sceneType == NEEduSceneTypeBig) {
        member.isBigClass = YES;
    }
    return member;
}
- (void)showChatViewWithitem:(NEEduMenuItem *)item {
    self.chatVC = [[NEEduChatViewController alloc] init];
    self.chatVC.messages = self.messages;
    self.chatVC.muteChat = [EduManager shared].profile.snapshot.room.states.muteChat.value;
    self.chatVC.modalPresentationStyle = UIModalPresentationFullScreen;
    [self presentViewController:self.chatVC animated:YES completion:nil];
    //更新菜单底部聊天室红点
    for (NEEduMenuItem *item in self.maskView.stackView.arrangedSubviews) {
        if (item.type == NEEduMenuItemTypeChat) {
            item.badgeLabel.hidden = YES;
            break;
        }
    }
}
//放在子类中实现
- (void)handsupItem:(NEEduMenuItem *)item {

}
- (void)setupShareKit {
    if (!self.shareHost) {
        NEScreenShareHostOptions *options = [[NEScreenShareHostOptions alloc] init];
        options.appGroup = kAppGroup;
        options.delegate = self;
        self.shareHost = [[NEScreenShareHost alloc] initWithOptions:options];
    }
}

// 音频流 视频流 屏幕共享辅流
- (void)onVideoStreamEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    [self updateAVEnable:enable user:user];
    if (self.membersVC) {
        for (NEEduMember *member in self.membersVC.members) {
            if ([member.userID isEqualToString:user.userUuid]) {
                member.hasVideo = enable;
                break;
            }
        }
    }
    if (self.membersVC.presentingViewController) {
        [self.membersVC reloadData];
    }
}
- (void)onAudioStreamEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    [self updateAVEnable:enable user:user];
    if (self.membersVC) {
        for (NEEduMember *member in self.membersVC.members) {
            if ([member.userID isEqualToString:user.userUuid]) {
                member.hasAudio = enable;
                break;
            }
        }
    }
    if (self.membersVC.presentingViewController) {
        [self.membersVC reloadData];
    }
}

- (void)updateAVEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    NSMutableArray *array = [NSMutableArray arrayWithArray:self.members];
    for (int i = 0; i < array.count; i++) {
        NEEduHttpUser *tmpUser = array[i];
        if ([tmpUser.userUuid isEqualToString:user.userUuid]) {
            [array replaceObjectAtIndex:i withObject:user];
        }
    }
    self.members = array;
    [self.collectionView reloadData];
    [self updateMyselfAVItemWithUser:user];
//    if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
//        for (int i = 0; i < self.maskView.stackView.arrangedSubviews.count; i ++) {
//            NEEduMenuItem *item = self.maskView.stackView.arrangedSubviews[i];
//            if (item.type == NEEduMenuItemTypeVideo) {
//                item.isSelected = !user.streams.video.value;
//            }
//            if (item.type == NEEduMenuItemTypeAudio) {
//                item.isSelected = !user.streams.audio.value;
//            }
//            if (item.type == NEEduMenuItemTypeShareScreen) {
//                item.isSelected = user.streams.subVideo.value;
//            }
//        }
//    }
}

- (void)onSubVideoStreamEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
        //FIXME:自己在共享的状态处理
//        if (self.isSharing) {
//            [self stopAllScreenShare];
//        }
//        [self startRecording];
        
        return;
    }
    NSMutableArray *members = [NSMutableArray arrayWithArray:self.members];
    for (int i = 0; i < members.count; i++) {
        NEEduHttpUser *tmpUser = members[i];
        if ([tmpUser.userUuid isEqualToString:user.userUuid]) {
            [members replaceObjectAtIndex:i withObject:user];
        }
    }
    self.members = members;
    // 设置屏幕共享画布
    if (enable) {
        NERtcVideoCanvasExtention *canvas = [[NERtcVideoCanvasExtention alloc] init];
        canvas.container = self.shareScreenView;
        canvas.uid = user.rtcUid;
        [[EduManager shared].videoService setupSubStreamVideo:canvas];
    }else {
        [[EduManager shared].videoService setupSubStreamVideo:nil];
    }
    self.shareScreenView.hidden = !enable;
}

- (void)onVideoAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
        [self.view makeToast:[NSString stringWithFormat:@"老师%@了你的摄像头",enable ? @"打开" : @"关闭"]];
        [self turnOnVideo:enable];
    }
}
- (void)onAudioAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
        [self.view makeToast:[NSString stringWithFormat:@"老师%@了你的麦克风",enable ? @"打开" : @"关闭"]];
        [self turnOnAudio:enable];
    }
}
- (void)onWhiteboardAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    NSMutableArray *members = [NSMutableArray arrayWithArray:self.members];
    for (int i = 0; i < members.count; i++) {
        NEEduHttpUser *tmpUser = members[i];
        if ([tmpUser.userUuid isEqualToString:user.userUuid]) {
            [members replaceObjectAtIndex:i withObject:user];
        }
    }
    self.members = members;
    self.whiteboardWritable = enable;
    //如果是自己的权限被修改 设置白板
    if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
        NSString *toast = enable?@"老师授予了你白板权限":@"老师取消了你白板权限";
        [self.view makeToast:toast];
        [[NMCWhiteboardManager sharedManager] callEnableDraw:self.whiteboardWritable];
        [[NMCWhiteboardManager sharedManager] hiddenTools:!self.whiteboardWritable];
    }
    
    //更新membervc
    if (self.membersVC) {
        for (NEEduMember *member in self.membersVC.members) {
            if ([member.userID isEqualToString:user.userUuid]) {
                member.whiteboardEnable = self.whiteboardWritable;
                break;
            }
        }
    }
    if (self.membersVC.presentingViewController) {
        [self.membersVC reloadData];
    }
}
- (void)onScreenShareAuthorizationEnable:(BOOL)enable user:(NEEduHttpUser *)user {
    NSMutableArray *members = [NSMutableArray arrayWithArray:self.members];
    for (int i = 0; i < members.count; i++) {
        NEEduHttpUser *tmpUser = members[i];
        if ([tmpUser.userUuid isEqualToString:user.userUuid]) {
            [members replaceObjectAtIndex:i withObject:user];
        }
    }
    self.members = members;
    //如果是自己的权限被修改 更新底部菜单栏
    if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
        NSString *toast = enable?@"老师授予了你共享权限":@"老师取消了你共享权限";
        [self.view makeToast:toast];
        if (enable) {
            NEEduMenuItem *shareItem = [[NEEduMenuItem alloc] initWithTitle:@"共享屏幕" image:[UIImage ne_imageNamed:@"menu_share_screen"]];
            shareItem.type = NEEduMenuItemTypeShareScreen;
            [shareItem setSelctedImage:[UIImage ne_imageNamed:@"menu_share_screen_stop"]];
            [self.maskView insertItem:shareItem atIndex:2];
        }else {
            [self.maskView removeItemType:NEEduMenuItemTypeShareScreen];
            [self stopRecord];
        }
    }
    //更新membervc
    if (self.membersVC) {
        for (NEEduMember *member in self.membersVC.members) {
            if ([member.userID isEqualToString:user.userUuid]) {
                member.shareScreenEnable = enable;
                break;
            }
        }
    }
    if (self.membersVC.presentingViewController) {
        [self.membersVC reloadData];
    }
}

- (void)onLessonStateChange:(NEEduLessonStep *)step roomUuid:(NSString *)roomUuid {
    //0/1/2 (初始化/已开始/已结束)
    [self.maskView.navView updateRoomState:self.room serverTime:step.time];
    if (step.value == NEEduLessonStateClassOver) {
        [self classOver];
        return;
    }
    if ([EduManager shared].localUser.roleType == NEEduRoleTypeStudent) {
        if (step.value == NEEduLessonStateClassIn) {
            self.lessonStateView.hidden = YES;
        }
    }
}

- (void)onLessonMuteAllText:(BOOL)mute roomUuid:(NSString *)roomUuid {
    if ([EduManager shared].localUser.roleType == NEEduRoleTypeStudent) {
        if (self.chatVC) {
            [self.chatVC updateMuteChat:mute];
        }
        NSString *string = mute ? @"聊天室已全体禁言" : @"聊天室已取消全体禁言";
        [[UIApplication sharedApplication].keyWindow makeToast:string];
    }
}
#pragma mark- NEEduVideoServiceDelegate
- (void)onRtcDisconnectWithReason:(NERtcError *)reason {
    [self.view makeToast:@"音视频断开连接"];
    [self leaveClass];
}

- (void)onNetQuality:(NERtcNetworkQualityStats *)quality {
    if (!self.netReachable) {
        self.maskView.navView.netStateView.image = [UIImage ne_imageNamed:@"net_0"];
        return;
    }
    if (quality.userId == [EduManager shared].localUser.rtcUserId) {
        int level = 0;
        NERtcNetworkQuality max = MAX(quality.txQuality, quality.rxQuality);
        switch (max) {
            case kNERtcNetworkQualityExcellent:
            case kNERtcNetworkQualityGood:
                level = 3;
                break;
            case kNERtcNetworkQualityPoor:
            case kNERtcNetworkQualityBad:
                level = 2;
                break;
            case kNERtcNetworkQualityVeryBad:
            case kNERtcNetworkQualityDown:
                level = 1;
                break;
            default:
                break;
        }
        NSString *string = [NSString stringWithFormat:@"net_%d",level];
        self.maskView.navView.netStateView.image = [UIImage ne_imageNamed:string];
    }
}

#pragma mark - NEEduRoomViewMaskViewDelegate
//结束课堂按钮
- (void)classOverBack {
    [self dismissViewControllerAnimated:YES completion:nil];
}
//返回按钮
- (void)backEvent {
    [self.view showAlertViewOnVC:self withTitle:@"确认离开课堂" subTitle:@"离开教室后将暂停学习，需要等待您再次进入课堂后方可继续上课？" confirm:^{
        [self leaveClass];
    }];
}

- (void)leaveClass {
    [self dismissVC];
    //结束共享
    if (self.isSharing) {
        [self stopAllScreenShare];
    }
    //老师学生离开音视频、聊天室、白板
    [[EduManager shared] leaveClassroom];
    [[NMCWhiteboardManager sharedManager] callWebLogoutIM];
    [[NMCWhiteboardManager sharedManager] clearWebViewCache];
    [[EduManager shared] destoryClassroom];
}
- (void)dismissVC {
    if (self.chatVC) {
        [self.chatVC dismissViewControllerAnimated:YES completion:nil];
    }
    if (self.membersVC) {
        [self.membersVC dismissViewControllerAnimated:YES completion:nil];
    }
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)classOver {
    //展示课堂结束页面
    self.classOverView.hidden = NO;
    if (self.chatVC) {
        [self.chatVC dismissViewControllerAnimated:YES completion:nil];
    }
    if (self.membersVC) {
        [self.membersVC dismissViewControllerAnimated:YES completion:nil];
    }
    if (self.isSharing) {
        [self stopAllScreenShare];
    }
    
    [[EduManager shared] leaveClassroom];
    [[NMCWhiteboardManager sharedManager] callWebLogoutIM];
    [[NMCWhiteboardManager sharedManager] clearWebViewCache];
    [[EduManager shared] destoryClassroom];
}


- (void)onSectionStateChangeAtIndex:(NSInteger)index item:(NEEduMenuItem *)item {
    [self showViewWithItem:item];
}
- (void)rightButton:(UIButton *)button selected:(BOOL)selected {
    if ([EduManager shared].localUser.roleType == NEEduRoleTypeTeacher) {
        int state = selected ? 2 : 1;
        NSString *title = selected ? @"结束课堂":@"确认开始上课";
        NSString *msg = selected ? @"结束课堂后老师和学生均会跳转课堂结束画面，支持查看课程回放":@"开始教学内容将同步至学生端，并正式开始课堂录制";
        __weak typeof(self)weakSelf = self;
        [weakSelf.view showAlertViewOnVC:weakSelf withTitle:title subTitle:msg confirm:^{
            button.userInteractionEnabled = NO;
            [[EduManager shared].roomService startLesson:state completion:^(NSError * _Nonnull error, NEEduPropertyItem * _Nonnull item) {
                button.userInteractionEnabled = YES;
                if (error) {
                    [weakSelf.view makeToast:error.localizedDescription];
                }else {
                    [weakSelf.maskView selectButton:!selected];
                }
            }];
        }];
    }
}

- (void)infoButtonClick:(UIButton *)button {
    self.infoView.hidden = !self.infoView.hidden;
}
#pragma mark - NEEduIMChatDelegate
- (void)didRecieveChatMessages:(NSArray<NIMMessage *> *)messages {
    //接收成功
    NIMMessage *imMessage = messages.firstObject;
    NIMMessageChatroomExtension *ext = imMessage.messageExt;
    NEEduChatMessage *message = [[NEEduChatMessage alloc] init];
    message.userName = ext.roomNickname;
    message.content = imMessage.text;
    message.myself = NO;
    message.textSize = [imMessage.text sizeWithWidth:(self.view.bounds.size.width - 112) font:[UIFont systemFontOfSize:14]];
    message.timestamp = [[NSDate date] timeIntervalSince1970];
    message.type = NEEduChatMessageTypeText;
    
    [self addTimeMessage:message];
    
    [self.messages addObject:message];
    if (self.chatVC.presentingViewController) {
        [self.chatVC reloadTableViewToBottom:YES];
    }else {
        for (NEEduMenuItem *item in self.maskView.stackView.arrangedSubviews) {
            if (item.type == NEEduMenuItemTypeChat) {
                item.badgeLabel.hidden = NO;
                break;
            }
        }
    }
}
- (void)didSendMessage:(NIMMessage *)message error:(NSError *)error {
    //发送成功
    if (error) {
        [self.view makeToast:@"消息发送失败"];
    }else {
        NEEduChatMessage *eduMessage = [[NEEduChatMessage alloc] init];
        NSString *role = [EduManager shared].localUser.roleType == NEEduRoleTypeTeacher ? @"老师":@"学生";
        eduMessage.userName = [NSString stringWithFormat:@"%@(%@)",[EduManager shared].localUser.userName,role];
        eduMessage.content = message.text;
        eduMessage.myself = YES;
        eduMessage.textSize = [message.text sizeWithWidth:(self.view.bounds.size.width - 112) font:[UIFont systemFontOfSize:14]];
        eduMessage.timestamp = [[NSDate date] timeIntervalSince1970];
        eduMessage.type = NEEduChatMessageTypeText;
        
        [self addTimeMessage:eduMessage];
        
        [self.messages addObject:eduMessage];
        if (self.chatVC) {
            [self.chatVC reloadTableViewToBottom:YES];
        }
//        [self.tableView reloadData];
//        if (self.messages.count > 0) {
//            [self.tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:[self.messages count] - 1 inSection:0] atScrollPosition:UITableViewScrollPositionBottom animated:YES];
//         }
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
            timeMessage.textSize = [timeMessage.content sizeWithWidth:(self.view.bounds.size.width - 112) font:[UIFont systemFontOfSize:14]];
            timeMessage.type = NEEduChatMessageTypeTime;
            [self.messages addObject:timeMessage];
        }
    }
}
#pragma mark - NEEduRoomServiceDelegate
- (void)netStateChangeWithState:(AFNetworkReachabilityStatus)state {
    if (state == AFNetworkReachabilityStatusNotReachable || state == AFNetworkReachabilityStatusUnknown) {
        //断网
        //网络展示无网
        self.netReachable = NO;
    }else {
        //有网
        // 请求房间快照
        self.netReachable = YES;
        __weak typeof(self)weakSelf = self;
        [[EduManager shared].roomService getRoomProfile:self.room.roomUuid completion:^(NSError * _Nonnull error, NEEduRoomProfile * _Nonnull profile) {
            weakSelf.room = profile.snapshot.room;
            /**
             1.房间状态刷新
             2.更新学生列表
             3.底部音视频按钮状态更新
             4.更新屏幕共享
             5.更新举手 上台
             6.更新成员列表
             */
            [weakSelf updateUIWithRoom:profile.snapshot.room];
            [weakSelf membersWithProfile:profile];
            [weakSelf updateAVItemWithProfile:profile];
            [weakSelf updateScreenShare];
            [weakSelf updateHandsupStateWithProfile:profile];
            [weakSelf updateMemberVCWithProfile:profile];
        }];
    }
}
- (void)updateUIWithRoom:(NEEduHttpRoom *)room {
    //FIXME:房间信息 房间状态
    [self.maskView.navView updateRoomState:room serverTime:[EduManager shared].profile.ts];
    //初始化上课按钮
    if ([EduManager shared].localUser.roleType == NEEduRoleTypeTeacher) {
        //开始上课/结束上课
        if ([EduManager shared].profile.snapshot.room.states.step.value ==  NEEduLessonStateClassIn) {
            [self.maskView selectButton:YES];
        }else {
            [self.maskView selectButton:NO];
        }
    }else {
//        学生在线的话 下讲台
        [self.maskView.startLesson setTitle:@"下讲台" forState:UIControlStateSelected];
        self.maskView.startLesson.hidden = YES;
        if ([EduManager shared].profile.snapshot.room.states.step.value == NEEduLessonStateClassIn) {
            self.lessonStateView.hidden = YES;
        }else {
            self.lessonStateView.hidden = NO;
        }
    }
}
- (NSArray <NEEduHttpUser *>*)membersWithProfile:(NEEduRoomProfile *)profile {
    return nil;
}
- (void)updateWhiteboardWritable:(BOOL)writable {
    if (self.whiteboardWritable != writable) {
        self.whiteboardWritable = writable;
        [[NMCWhiteboardManager sharedManager] callEnableDraw:self.whiteboardWritable];
        [[NMCWhiteboardManager sharedManager] hiddenTools:!self.whiteboardWritable];
    }
}
- (void)updateAVItemWithProfile:(NEEduRoomProfile *)profile {
    for (NEEduHttpUser *user in profile.snapshot.members) {
        if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
            [self updateMyselfAVItemWithUser:user];
            [self updateWhiteboardWritable:user.properties.whiteboard.drawable];
            break;
        }
    }
}

- (void)updateScreenShare {
    NEEduHttpUser *user = [[EduManager shared].userService userIsShareScreen];
    if (user) {
        //共享按钮状态改变为选择
        [self onSubVideoStreamEnable:YES user:user];
    }
}
- (void)updateHandsupStateWithProfile:(NEEduRoomProfile *)profile {
    //子类中实现
}
- (void)updateMemberVCWithProfile:(NEEduRoomProfile *)profile {
    if (self.membersVC) {
        NSMutableArray *memberArray = [NSMutableArray array];
        for (NEEduHttpUser *user in profile.snapshot.members) {
            if (![user.role isEqualToString:NEEduRoleHost]) {
                NEEduMember *member = [self memberFromHttpUser:user];
                [memberArray addObject:member];
            }
        }
        self.membersVC.members = memberArray;
        self.membersVC.muteChat = profile.snapshot.room.states.muteChat.value;
        if (self.membersVC.presentingViewController) {
            [self.membersVC loadData];
            [self.membersVC reloadData];
        }
    }
}
#pragma mark - private
- (void)updateMyselfAVItemWithUser:(NEEduHttpUser *)user {
    if ([user.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
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
- (void)showAlertViewWithMember:(NEEduHttpUser *)member cell:(NEEduVideoCell *)cell {
    if ([EduManager shared].localUser.roleType != NEEduRoleTypeTeacher) {
        //只有老师角色有操作权限
        return;
    }
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil message:@"更多操作" preferredStyle:UIAlertControllerStyleActionSheet];
    NSString *audioTitle = member.streams.audio.value ? @"静音":@"取消静音";
    NSString *videoTitle = member.streams.video.value ? @"关闭摄像头":@"开启摄像头";
    UIAlertAction *audioAction = [UIAlertAction actionWithTitle:audioTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        __weak typeof(self)weakSelf = self;
        if ([member.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
            [[EduManager shared].userService localUserAudioEnable:!member.streams.audio.value result:^(NSError * _Nonnull error) {
                if (error) {
                    [weakSelf.view makeToast:error.description];
                }else {
                    [weakSelf.view makeToast:@"操作成功"];
                }
            }];
        }else {
            [[EduManager shared].userService remoteUserAudioEnable:!member.streams.audio.value userID:member.userUuid result:^(NSError * _Nonnull error) {
                if (error) {
                    [weakSelf.view makeToast:error.description];
                }else {
                    [weakSelf.view makeToast:@"操作成功"];
                }
            }];
        }
    }];
    UIAlertAction *videoAction = [UIAlertAction actionWithTitle:videoTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        __weak typeof(self)weakSelf = self;
        if ([member.userUuid isEqualToString:[EduManager shared].localUser.userUuid]) {
            [[EduManager shared].userService localUserVideoEnable:!member.streams.video.value result:^(NSError * _Nonnull error) {
                if (error) {
                    [weakSelf.view makeToast:error.description];
                }else {
                    [weakSelf.view makeToast:@"操作成功"];
                }
            }];
        }else {
            [[EduManager shared].userService remoteUserVideoEnable:!member.streams.video.value userID:member.userUuid result:^(NSError * _Nonnull error) {
                if (error) {
                    [weakSelf.view makeToast:error.description];
                }else {
                    [weakSelf.view makeToast:@"操作成功"];
                }
            }];
        }
    }];
    [alert addAction:audioAction];
    [alert addAction:videoAction];
    
    if (![member.role isEqualToString:NEEduRoleHost]) {
        NSString *whiteboardTitle = member.properties.whiteboard.drawable ? @"取消白板权限":@"授予白板权限";
        NSString *shareTitle = member.properties.screenShare.value ? @"取消共享权限":@"授予共享权限";
        UIAlertAction *whiteboardAction = [UIAlertAction actionWithTitle:whiteboardTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            if ([EduManager shared].profile.snapshot.room.states.step.value != NEEduLessonStateClassIn) {
                [self.view makeToast:@"还未开始上课"];
                return;
            }
            //授予/取消白板权限
            __weak typeof(self)weakSelf = self;
            [[EduManager shared].userService whiteboardDrawable:!member.properties.whiteboard.drawable userID:member.userUuid result:^(NSError * _Nonnull error) {
                if (!error) {
                    [weakSelf.view makeToast:@"授权白板权限成功"];
                }else {
                    [weakSelf.view makeToast:error.description];
                }
            }];
        }];
        UIAlertAction *shareAction = [UIAlertAction actionWithTitle:shareTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            if ([EduManager shared].profile.snapshot.room.states.step.value != NEEduLessonStateClassIn) {
                [self.view makeToast:@"还未开始上课"];
                return;
            }
            //授予/取消屏幕共享
            __weak typeof(self)weakSelf = self;
            [[EduManager shared].userService screenShareAuthorization:!member.properties.screenShare.value userID:member.userUuid result:^(NSError * _Nonnull error) {
                if (!error) {
                    [weakSelf.view makeToast:@"授权屏幕共享权限成功"];
                }else {
                    [weakSelf.view makeToast:error.description];
                }
            }];
        }];
        [alert addAction:whiteboardAction];
        [alert addAction:shareAction];
        
        if (member.properties.avHandsUp.value == NEEduHandsupStateTeaAccept) {
            UIAlertAction *offStage = [UIAlertAction actionWithTitle:@"请他下台" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
                __weak typeof(self)weakSelf = self;
                [[EduManager shared].userService handsupStateChange:NEEduHandsupStateTeaOffStage userID:member.userUuid result:^(NSError * _Nonnull error) {
                    if (!error) {
                        [weakSelf.view makeToast:@"操作成功"];
                    }else {
                        [weakSelf.view makeToast:error.localizedDescription];
                    }
                }];
            }];
            [alert addAction:offStage];
        }
    }
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
    }];
    
    [alert addAction:cancelAction];
    UIPopoverPresentationController *popover = alert.popoverPresentationController;
    if (popover) {
        popover.sourceView = cell;
        popover.permittedArrowDirections = UIPopoverArrowDirectionAny;
    }
    [self presentViewController:alert animated:YES completion:nil];
}

#pragma mark - NEEduVideoCellDelegate
- (void)didTapCell:(NEEduVideoCell *)cell {
    if (cell.member.userUuid) {
        [self showAlertViewWithMember:cell.member cell:cell];
    }
}
#pragma mark - UICollectionViewDataSource
- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.members.count;
}

- (__kindof UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    NEEduVideoCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:cellID forIndexPath:indexPath];
    NEEduHttpUser *user = self.members[indexPath.row];
    cell.delegate = self;
    cell.member = user;
    return cell;
}
#pragma mark - get

- (NEEduRoomViewMaskView *)maskView {
    if (!_maskView) {
        _maskView = [[NEEduRoomViewMaskView alloc] initWithMenuItems:self.menuItems];
        _maskView.delegate = self;
    }
    return _maskView;
}
- (UICollectionView *)collectionView {
    if (!_collectionView) {
        UICollectionViewFlowLayout *layout = [[UICollectionViewFlowLayout alloc] init];
        layout.itemSize = CGSizeMake(120, 90);
        layout.minimumLineSpacing = 10;
        layout.footerReferenceSize = CGSizeMake(120, 48);
        _collectionView = [[UICollectionView alloc] initWithFrame:CGRectZero collectionViewLayout:layout];
        _collectionView.translatesAutoresizingMaskIntoConstraints = NO;
        _collectionView.backgroundColor = [UIColor whiteColor];
        _collectionView.delegate = self;
        _collectionView.dataSource = self;
        [_collectionView registerClass:[NEEduVideoCell class] forCellWithReuseIdentifier:cellID];
        _collectionView.backgroundColor = [UIColor colorWithRed:26/255.0 green:32/255.0 blue:40/255.0 alpha:1.0];
    }
    return _collectionView;
}
- (UIView *)shareScreenView {
    if (!_shareScreenView) {
        _shareScreenView = [[UIView alloc] init];
        _shareScreenView.hidden = YES;
        _shareScreenView.translatesAutoresizingMaskIntoConstraints = NO;
    }
    return _shareScreenView;
}
- (NEEduLessonStateView *)lessonStateView {
    if (!_lessonStateView) {
        _lessonStateView = [[NEEduLessonStateView alloc] init];
        _lessonStateView.hidden = YES;
    }
    return _lessonStateView;
}
- (NEEduLessonOverView *)classOverView {
    if (!_classOverView) {
        _classOverView = [[NEEduLessonOverView alloc] init];
        [_classOverView.backButton addTarget:self action:@selector(classOverBack) forControlEvents:UIControlEventTouchUpInside];
        _classOverView.hidden = YES;
    }
    return _classOverView;
}
- (NSMutableArray<NEEduChatMessage *> *)messages {
    if (!_messages) {
        _messages = [NSMutableArray array];
    }
    return _messages;
}
- (NEEduLessonInfoView *)infoView {
    if (!_infoView) {
        _infoView = [[NEEduLessonInfoView alloc] init];
        NSString *lessonID = [self.room.roomUuid substringToIndex:self.room.roomUuid.length - 1];
        self.infoView.lessonItem.titleLabel.text = lessonID;
        self.infoView.lessonName.text = self.room.roomName;
        NEEduHttpUser *user = self.members.firstObject;
        self.infoView.teacherName.text = user.userName;
        [self.view addSubview:_infoView];
        NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:_infoView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeTop multiplier:1.0 constant:0];
        NSLayoutConstraint *right = [NSLayoutConstraint constraintWithItem:_infoView attribute:NSLayoutAttributeRight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeRight multiplier:1.0 constant:-0];
        NSLayoutConstraint *width = [NSLayoutConstraint constraintWithItem:_infoView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeWidth multiplier:1.0 constant:0];
        NSLayoutConstraint *height = [NSLayoutConstraint constraintWithItem:_infoView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationEqual toItem:self.view attribute:NSLayoutAttributeHeight multiplier:1.0 constant:0];
        [self.view addConstraints:@[top,right,width,height]];
    }
    return _infoView;
}
#pragma mark - whiteboard
- (WKWebView *)boardView {
    if (!_boardView) {
        _boardView = [[NMCWhiteboardManager sharedManager] createWebViewFrame:CGRectZero];
        [NMCWhiteboardManager sharedManager].delegate = self;
        _boardView.translatesAutoresizingMaskIntoConstraints = NO;
        NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:NMCWhiteboardURL]];
        [_boardView loadRequest:request];
        if([[[UIDevice currentDevice] systemVersion] floatValue] < 12.0) {
            if (@available(iOS 11.0, *)) {
                _boardView.scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
            }
        }
        if (@available(iOS 12.0, *)) {
            _boardView.scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentAutomatic;
        }
    }
    return _boardView;
}


#pragma mark - NMCWhiteboardManagerDelegate
- (void)onWebPageLoaded {
    NMCWebLoginParam *param = [[NMCWebLoginParam alloc] init];
    param.appKey = [EduManager shared].localUser.imKey;
    param.account = [EduManager shared].localUser.userUuid;
    param.token = [EduManager shared].localUser.imToken;
    param.ownerAccount = [EduManager shared].profile.snapshot.room.properties.chatRoom.roomCreatorId;
    param.channelName = [EduManager shared].profile.snapshot.room.properties.whiteboard.channelName;
    param.record = YES;
    param.debug = NO;
    param.nickname = [EduManager shared].localUser.userName;
    [[NMCWhiteboardManager sharedManager] callWebLoginIM:param];
}

- (void)onWebLoginIMSucceed {
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
    WhiteboardItem *visionLock = [[WhiteboardItem alloc] initWithName:WhiteboardItemNameVisionLock];

    moreItem.subItems = @[eraserItem,clearItem,undoItem,redoItem];
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
    [[NMCWhiteboardManager sharedManager] hiddenTools:!self.whiteboardWritable];
}

- (void)onWebLoginIMFailed:(NSInteger)code error:(NSString *)error {
    NSLog(@"wb:onWebLoginIMFailed : %ld, %@",(long)code, error);
    [self showToastView:error];
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
    [self.boardView reload];
    [[NMCWhiteboardManager sharedManager] clearWebViewCache];
}

#pragma mark - Orientations
-(BOOL)shouldAutorotate {
    return NO;
}
- (UIInterfaceOrientationMask)supportedInterfaceOrientations {
     return UIInterfaceOrientationMaskLandscapeRight;
}
- (UIInterfaceOrientation)preferredInterfaceOrientationForPresentation {
    return UIInterfaceOrientationLandscapeRight;
}
- (void)dealloc
{
    [[UIApplication sharedApplication] setIdleTimerDisabled:NO];
}
@end
